import { Component, OnInit } from "@angular/core";
import { Marketplace } from "app/main/content/_model/marketplace";
import { MatTableDataSource } from "@angular/material";
import {
  ClassDefinition,
  ClassArchetype,
} from "app/main/content/_model/meta/class";
import { Helpseeker } from "app/main/content/_model/helpseeker";
import { Tenant } from "app/main/content/_model/tenant";
import { FormBuilder } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { LoginService } from "app/main/content/_service/login.service";
import { CoreHelpSeekerService } from "app/main/content/_service/core-helpseeker.service";
import { ClassDefinitionService } from "app/main/content/_service/meta/core/class/class-definition.service";
import { TenantService } from "app/main/content/_service/core-tenant.service";
import { isNullOrUndefined } from "util";

@Component({
  templateUrl: "./task-select.component.html",
  styleUrls: ["./task-select.component.scss"],
})
export class FuseTaskSelectComponent implements OnInit {
  marketplace: Marketplace;
  dataSource = new MatTableDataSource<ClassDefinition>();
  displayedColumns = ["name", "configuration", "tId"];
  helpseeker: Helpseeker;
  tenant: Tenant;

  constructor(
    formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private loginService: LoginService,
    private coreHelpSeekerService: CoreHelpSeekerService,
    private classDefinitionService: ClassDefinitionService,
    private tenantService: TenantService
  ) {}

  async ngOnInit() {
    this.helpseeker = <Helpseeker>(
      await this.loginService.getLoggedIn().toPromise()
    );

    this.tenant = <Tenant>(
      await this.tenantService.findById(this.helpseeker.tenantId).toPromise()
    );

    this.tenantService.initHeader(this.tenant);

    this.marketplace = <Marketplace>(
      await this.coreHelpSeekerService
        .findRegisteredMarketplaces(this.helpseeker.id)
        .toPromise()
    );

    if (!isNullOrUndefined(this.marketplace)) {
      let tasks = <ClassDefinition[]>(
        await this.classDefinitionService
          .getByArchetype(
            this.marketplace,
            ClassArchetype.TASK,
            this.helpseeker.tenantId
          )
          .toPromise()
      );
      // TODO
      this.dataSource.data = tasks
        .filter((t) => t.configurationId != null)
        .sort((c1, c2) => c1.configurationId.localeCompare(c2.configurationId));
      console.error(this.dataSource.data);
    }
  }

  onRowSelect(row) {
    this.router.navigate(
      [`main/configurator/instance-editor/${this.marketplace.id}`],
      { queryParams: { 0: row.id } }
    );
  }

  private isFF() {
    return this.tenant.name == "FF Eidenberg";
  }

  private isMV() {
    return this.tenant.name === "Musikverein_Schwertberg";
  }
  private isOther() {
    return !this.isFF() && !this.isMV();
  }
}
