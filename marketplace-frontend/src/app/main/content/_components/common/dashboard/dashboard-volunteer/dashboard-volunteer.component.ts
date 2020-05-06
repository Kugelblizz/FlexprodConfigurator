import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ShareDialog } from "./share-dialog/share-dialog.component";
import { CoreVolunteerService } from "../../../../_service/core-volunteer.service";
import { LoginService } from "../../../../_service/login.service";
import { isNullOrUndefined } from "util";
import { Marketplace } from "../../../../_model/marketplace";
import { ClassInstanceService } from "../../../../_service/meta/core/class/class-instance.service";
import { ClassInstanceDTO, } from "../../../../_model/meta/class";
import { CoreUserImagePathService } from "../../../../_service/core-user-imagepath.service";
import { CoreHelpSeekerService } from "../../../../_service/core-helpseeker.service";
import { MatSort, MatPaginator, Sort, MatIconRegistry } from "@angular/material";
import { TenantService } from "../../../../_service/core-tenant.service";
import { Volunteer } from "../../../../_model/volunteer";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { Tenant } from "app/main/content/_model/tenant";
import { LocalRepositoryService } from 'app/main/content';
import { timer } from 'rxjs';

@Component({
  selector: "dashboard-volunteer",
  templateUrl: "./dashboard-volunteer.component.html",
  styleUrls: ["./dashboard-volunteer.component.scss"],
})
export class DashboardVolunteerComponent implements OnInit {
  volunteer: Volunteer;
  marketplace: Marketplace;

  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  isLoaded: boolean;

  tableDataSource = new MatTableDataSource<ClassInstanceDTO>();
  selectedTenants: Tenant[] = [];

  private displayedColumnsRepository: string[] = [
    "issuer",
    "taskName",
    "taskType1",
    "date",
    "action"
  ];

  image;

  subscribedTenants: Tenant[] = [];
  allTenants: Tenant[] = [];

  marketplaceClassInstances: ClassInstanceDTO[] = [];
  localClassInstances: ClassInstanceDTO[] = [];
  filteredClassInstances: ClassInstanceDTO[] = [];

  isLocalRepositoryConnected: boolean;
  timeout: boolean = false;

  constructor(
    public dialog: MatDialog,
    private coreHelpseekerService: CoreHelpSeekerService,
    private loginService: LoginService,
    private classInstanceService: ClassInstanceService,
    private userImagePathService: CoreUserImagePathService,
    private localRepositoryService: LocalRepositoryService,
    private volunteerService: CoreVolunteerService,
    private tenantService: TenantService,
    private sanitizer: DomSanitizer,
    private router: Router,
    iconRegistry: MatIconRegistry
  ) { 
    iconRegistry.addSvgIcon(
      'info',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/info.svg'));
  }

  async ngOnInit() {
    let t = timer(3000);
    t.subscribe(() => {
      this.timeout = true;
    });

    this.volunteer = <Volunteer>(
      await this.loginService.getLoggedIn().toPromise()
    );
    this.setVolunteerImage();

    this.subscribedTenants = <Tenant[]>(
      await this.tenantService.findByVolunteerId(this.volunteer.id).toPromise()
    );

    this.allTenants = <Tenant[]>(
      await this.tenantService.findAll().toPromise()
    );

    this.isLocalRepositoryConnected = await this.localRepositoryService.isConnected(this.volunteer);
    if (this.isLocalRepositoryConnected) {
      let marketplaces = <Marketplace[]>(
        await this.volunteerService.findRegisteredMarketplaces(this.volunteer.id).toPromise()
      );
      this.marketplace = marketplaces[0];

      this.marketplaceClassInstances = <ClassInstanceDTO[]>(
        await this.classInstanceService.getUserClassInstancesByArcheType(this.marketplace, 'TASK', this.volunteer.id, this.volunteer.subscribedTenants).toPromise()
      );

      this.localClassInstances = <ClassInstanceDTO[]>(
        await this.localRepositoryService.findClassInstancesByVolunteer(this.volunteer).toPromise());

      // concat local and mp and remove dublicates
      this.filteredClassInstances = this.localClassInstances.concat(
        this.marketplaceClassInstances.filter(mp => this.localClassInstances.map(lo => lo.id).indexOf(mp.id) < 0));

      this.filteredClassInstances = this.filteredClassInstances.sort(
        (a, b) => {
          if (a.dateFrom && b.dateFrom) {
            return b.dateFrom.valueOf() - a.dateFrom.valueOf();
          }
        });

      this.tableDataSource.data = this.filteredClassInstances;
      this.paginator.length = this.filteredClassInstances.length;
      this.tableDataSource.paginator = this.paginator;

    }
  }

  setVolunteerImage() {
    let objectURL = "data:image/png;base64," + this.volunteer.image;
    this.image = this.sanitizer.bypassSecurityTrustUrl(objectURL);
  }

  navigateToTenantOverview() {
    this.router.navigate(["/main/dashboard/tenants"]);
  }


  getTenantImage(tenantId: string) {
    let tenant = this.allTenants.find(t => t.id === tenantId);
    if (isNullOrUndefined(tenant)) {
      return "/assets/images/avatars/profile.jpg";
    } else {
      return tenant.image;
    }
  }

  getIssuerName(tenantId: string) {
    let tenant = this.allTenants.find(t => t.id === tenantId);

    if(!isNullOrUndefined(tenant)) {
      return tenant.name;
    } else {
      return "";
    }
  }


  triggerShareDialog() {
    const dialogRef = this.dialog.open(ShareDialog, {
      width: "700px",
      height: "255px",
      data: { name: "share" },
    });

    dialogRef.afterClosed().subscribe((result: any) => { });
  }




  tenantSelectionChanged(selectedTenants: Tenant[]) {
    this.selectedTenants = selectedTenants;

    // concat local and mp and remove dublicates
    this.filteredClassInstances = this.localClassInstances.concat(
      this.marketplaceClassInstances.filter(mp => this.localClassInstances.map(lo => lo.id).indexOf(mp.id) < 0));

    this.filteredClassInstances = this.filteredClassInstances.filter(ci => {
      return this.selectedTenants.findIndex(t => t.id === ci.tenantId) >= 0;
    });

    this.filteredClassInstances.sort(
      (a, b) => {
        if (a.dateFrom && b.dateFrom) {
          return b.dateFrom.valueOf() - a.dateFrom.valueOf();
        }
      });

    this.tableDataSource.data = this.filteredClassInstances;
    this.paginator.length = this.filteredClassInstances.length;
    this.tableDataSource.paginator = this.paginator;
  }

  //---- Local Repository functions -----//

  inLocalRepository(classInstance: ClassInstanceDTO) {
    return this.localClassInstances.findIndex(t => t.id === classInstance.id) >= 0;
  }

  async syncOneToLocalRepository(classInstance: ClassInstanceDTO) {
    // let ci = <ClassInstance>await
    //   this.classInstanceService.getClassInstanceById(this.marketplace, classInstanceDTO.id, classInstanceDTO.tenantId).toPromise();
    // await this.localRepositoryService.synchronizeClassInstance(this.volunteer, ci).toPromise();
    // this.localClassInstances.push(ci);

    this.localClassInstances = <ClassInstanceDTO[]>await
      this.localRepositoryService.synchronizeSingleClassInstance(this.volunteer, classInstance).toPromise();
  }

  async removeOneFromLocalRepository(classInstance: ClassInstanceDTO) {
    this.localClassInstances = <ClassInstanceDTO[]>await
      this.localRepositoryService.removeSingleClassInstance(this.volunteer, classInstance).toPromise();


  }

  async syncAllToLocalRepository() {
    let missingClassInstances: ClassInstanceDTO[] = [];
    this.filteredClassInstances.forEach(ci => {
      if (!(this.localClassInstances.findIndex(t => t.id === ci.id) >= 0)) {
        // let ci = <ClassInstance>await
        //   this.classInstanceService.getClassInstanceById(this.marketplace, ci.id, ci.tenantId).toPromise();

        missingClassInstances.push(ci);
      }
    });

    this.localClassInstances = <ClassInstanceDTO[]>await
      this.localRepositoryService.synchronizeClassInstances(this.volunteer, missingClassInstances).toPromise();
  }


  async removeAllFromLocalRepository() {
    // let toRemoveClassInstances: ClassInstanceDTO[] = [];
    // this.filteredClassInstances.forEach(ci => {
    //   if (this.localClassInstances.findIndex(t => t.id === ci.id) >= 0) {
    //     toRemoveClassInstances.push(ci);
    //   }
    // });

    let newClassInstances: ClassInstanceDTO[] = [];

    newClassInstances = this.localClassInstances.filter(ci => {
      return (this.filteredClassInstances.findIndex(t => t.id === ci.id) === -1)
    });

    this.localClassInstances = <ClassInstanceDTO[]>await
      this.localRepositoryService.setClassInstances(this.volunteer, newClassInstances).toPromise();
  }


  sortData(sort: Sort) {
    this.tableDataSource.data = this.tableDataSource.data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      switch (sort.active) {
        case 'issuer': return this.compare(this.getIssuerName(a.issuerId), this.getIssuerName(b.issuerId), isAsc);
        case 'taskName': return this.compare(a.name, b.name, isAsc);
        case 'taskType1': return this.compare(a.taskType1, b.taskType1, isAsc);
        case 'date': return this.compare(a.dateFrom, b.dateFrom, isAsc);
        case 'action': return this.compare(this.inLocalRepository(a).toString(), this.inLocalRepository(b).toString(), isAsc);
        default: return 0;
      }
    });
  }

  compare(a: number | string | Date, b: number | string | Date, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

  navigateToClassInstanceDetails(row) {
    let classInstance = this.filteredClassInstances.find(ci => ci.id = row.id)
    this.router.navigate(['main/details/' + row.id + '/' + row.tenantId], {state:  {data: {classInstance}}});
  }

}

export interface DialogData {
  name: string;
}

