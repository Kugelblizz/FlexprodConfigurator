import { Component, OnInit } from "@angular/core";
import { TenantService } from "app/main/content/_service/core-tenant.service";
import { Tenant } from "app/main/content/_model/tenant";
import { ImageService } from "app/main/content/_service/image.service";
import { LoginService } from "app/main/content/_service/login.service";
import { timeout, timeInterval } from "rxjs/operators";
import { GlobalInfo } from "app/main/content/_model/global-info";
import { User, UserRole } from "app/main/content/_model/user";
import { CoreUserService } from "app/main/content/_service/core-user.serivce";
import { RoleChangeService } from "app/main/content/_service/role-change.service";

@Component({
  selector: "tenant-overview",
  templateUrl: "tenant-overview.component.html",
  styleUrls: ["tenant-overview.component.scss"],
})
export class TenantOverviewComponent implements OnInit {
  user: User;
  currentTenants: Tenant[] = [];
  currentRole: UserRole;
  allTenants: Tenant[] = [];

  isLoaded: boolean = false;

  constructor(
    private loginService: LoginService,
    private tenantService: TenantService,
    private imageService: ImageService,
    private coreUserService: CoreUserService,
    private roleChangeService: RoleChangeService
  ) {}

  async ngOnInit() {
    this.allTenants = <Tenant[]>await this.tenantService.findAll().toPromise();
    this.updateCurrentInfo();

    this.isLoaded = true;
  }

  getTenantImage(tenant: Tenant) {
    return this.imageService.getImgSourceFromBytes(tenant.image);
  }

  isSubscribed(tenant: Tenant, role: UserRole) {
    return (
      this.user.subscribedTenants
        .filter((s) => s.tenantId === tenant.id)
        .findIndex((t) => t.role === role) >= 0
    );
  }

  async unsubscribe(tenant: Tenant, role: UserRole) {
    this.user = <User>(
      await this.coreUserService
        .unsubscribeUserFromTenant(
          this.user.id,
          tenant.marketplaceId,
          tenant.id,
          role
        )
        .toPromise()
    );

    if (this.user.subscribedTenants.length == 0) {
      this.loginService.generateGlobalInfo(UserRole.NONE, []).then(() => {
        this.roleChangeService.changeRole(UserRole.NONE);
        this.updateCurrentInfo();
      });
    } else {
      if (
        // current role unssubscribed?
        this.currentRole == role &&
        this.currentTenants.length == 1 &&
        this.currentTenants.map((t) => t.id).indexOf(tenant.id) >= 0
      ) {
        this.loginService
          .generateGlobalInfo(this.user.subscribedTenants[0].role, [
            this.user.subscribedTenants[0].tenantId,
          ])
          .then(() => {
            this.roleChangeService.changeRole(
              this.user.subscribedTenants[0].role
            );
            this.updateCurrentInfo();
          });
      } else {
        this.loginService
          .generateGlobalInfo(
            this.currentRole,
            this.currentTenants.map((t) => t.id)
          )
          .then(() => {
            this.roleChangeService.update();
            this.updateCurrentInfo();
          });
      }
    }
  }

  async subscribe(tenant: Tenant, role: UserRole) {
    this.user = <User>(
      await this.coreUserService
        .subscribeUserToTenant(
          this.user.id,
          tenant.marketplaceId,
          tenant.id,
          role
        )
        .toPromise()
    );

    if (this.currentRole == UserRole.NONE) {
      this.loginService.generateGlobalInfo(role, [tenant.id]).then(() => {
        this.roleChangeService.changeRole(role);
        this.updateCurrentInfo();
      });
    } else {
      this.loginService
        .generateGlobalInfo(
          this.currentRole,
          this.currentTenants.map((t) => t.id)
        )
        .then(() => {
          this.roleChangeService.update();
          this.updateCurrentInfo();
        });
    }
  }

  async updateCurrentInfo() {
    let globalInfo = <GlobalInfo>(
      await this.loginService.getGlobalInfo().toPromise()
    );

    this.user = globalInfo.user;
    this.currentTenants = globalInfo.tenants;
    this.currentRole = globalInfo.userRole;
  }

  navigateBack() {
    window.history.back();
  }
}
