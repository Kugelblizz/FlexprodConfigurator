import { Component, OnInit, ViewChild, Inject } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { ClassInstanceService } from "app/main/content/_service/meta/core/class/class-instance.service";
import { ClassInstance } from "app/main/content/_model/meta/class";
import { LoginService } from "app/main/content/_service/login.service";
import { User, UserRole } from "app/main/content/_model/user";
import { Marketplace } from "app/main/content/_model/marketplace";
import {
  MatTableDataSource,
  MatSort,
  Sort,
  MAT_DIALOG_DATA,
} from "@angular/material";
import { TenantService } from "app/main/content/_service/core-tenant.service";
import { Tenant } from "app/main/content/_model/tenant";
import { PropertyInstance } from "app/main/content/_model/meta/property";
import { GlobalInfo } from "app/main/content/_model/global-info";
import { GlobalService } from "app/main/content/_service/global.service";
import { LocalRepositoryService } from "app/main/content/_service/local-repository.service";

@Component({
  selector: "app-class-instance-details",
  templateUrl: "./class-instance-details.component.html",
  styleUrls: ["./class-instance-details.component.scss"],
})
export class ClassInstanceDetailsComponent implements OnInit {
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  id: string = null;
  classInstance: ClassInstance;
  participant: User;
  role: UserRole;
  marketplace: Marketplace;
  tenant: Tenant;

  isDialog: boolean = false;
  isLocal: boolean = false;

  tableDataSource = new MatTableDataSource<PropertyInstance<any>>();
  displayedColumns = ["name", "values", "type"];

  constructor(
    private route: ActivatedRoute,
    private classInstanceService: ClassInstanceService,
    private loginService: LoginService,
    private tenantService: TenantService,
    private globalService: GlobalService,
    private localRepositoryService: LocalRepositoryService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.route.params.subscribe((params) => {
      this.id = params.id;
    });
  }

  async ngOnInit() {
    if (!this.id) {
      this.id = this.data.id;
      this.isDialog = true;
    }

    let globalInfo = <GlobalInfo>(
      await this.globalService.getGlobalInfo().toPromise()
    );

    this.participant = globalInfo.user;
    this.marketplace = globalInfo.marketplace;
    this.tenant = globalInfo.tenants[0];

    this.classInstance = <ClassInstance>(
      await this.classInstanceService
        .getClassInstanceById(this.marketplace, this.id)
        .toPromise()
    );

    if (this.classInstance === null) {
      let role = <UserRole>(
        await this.loginService.getLoggedInUserRole().toPromise()
      );

      if (role === UserRole.VOLUNTEER) {
        this.classInstance = <ClassInstance>(
          await this.localRepositoryService
            .getSingleClassInstance(this.participant, this.id)
            .toPromise()
        );
        this.isLocal = true;
      }
    }

    this.tableDataSource.data = this.classInstance.properties;

    this.tenantService.initHeader(this.tenant);
  }

  getName() {
    return this.classInstance.properties.find((p) => p.name === "name")
      .values[0];
  }

  sortData(sort: Sort) {
    this.tableDataSource.data = this.tableDataSource.data.sort((a, b) => {
      const isAsc = sort.direction === "asc";
      switch (sort.active) {
        case "name":
          return this.compare(a.name, b.name, isAsc);
        default:
          return 0;
      }
    });
  }

  compare(a: number | string, b: number | string, isAsc: boolean) {
    if (typeof a === "string" && typeof b === "string") {
      return (a.toLowerCase() < b.toLowerCase() ? -1 : 1) * (isAsc ? 1 : -1);
    } else {
      return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
    }
  }
}
