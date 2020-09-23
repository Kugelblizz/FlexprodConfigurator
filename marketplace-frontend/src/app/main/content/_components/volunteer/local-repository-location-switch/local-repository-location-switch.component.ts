import { Component, OnInit } from "@angular/core";
import { Location } from "@angular/common";
import { DropboxUtils } from "./dropbox-utils";
import { Dropbox } from "dropbox";
import { LoginService } from "app/main/content/_service/login.service";
import { User, LocalRepositoryLocation } from "app/main/content/_model/user";
import { GlobalInfo } from "app/main/content/_model/global-info";
import { CoreUserService } from "app/main/content/_service/core-user.service";
import { LocalRepositoryJsonServerService } from "app/main/content/_service/local-repository-jsonServer.service";
import { LocalRepositoryDropboxService } from "app/main/content/_service/local-repository-dropbox.service";
import { ClassInstance } from "app/main/content/_model/meta/class";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";
import { NextcloudCredentials } from "app/main/content/_model/nextcloud-credentials";
import { LocalRepositoryNextcloudService } from "app/main/content/_service/local-repository-nextcloud.service";
import { environment } from "environments/environment";

@Component({
  selector: "app-local-repository-location-switch",
  templateUrl: "./local-repository-location-switch.component.html",
  styleUrls: ["./local-repository-location-switch.component.scss"],
})
export class LocalRepositoryLocationSwitchComponent implements OnInit {
  DROPBOX_CLIENT_ID: string = "ky4bainncqhjjn8";
  REDIRECT_URI = environment.REDIRECT_URI;

  globalInfo: GlobalInfo;
  user: User;
  FILE_NAME: string = "db.json";

  isJsonServerConnected: boolean = false;

  dropboxAuthUrl: string;
  isDropboxConnected: boolean = false;

  isNextcloudConnected: boolean = false;
  nextcloudLoginForm: FormGroup;
  nextcloudLoginError: boolean;

  isLoaded: boolean = false;

  constructor(
    private location: Location,
    private loginService: LoginService,
    private coreUserService: CoreUserService,
    private formBuilder: FormBuilder,
    private localRepoJsonServerService: LocalRepositoryJsonServerService,
    private localRepoDropboxService: LocalRepositoryDropboxService,
    private localRepoNextcloudService: LocalRepositoryNextcloudService
  ) {}

  async ngOnInit() {
    this.globalInfo = <GlobalInfo>(
      await this.loginService.getGlobalInfo().toPromise()
    );
    this.user = this.globalInfo.user;

    let dbx = new Dropbox({ clientId: this.DROPBOX_CLIENT_ID });
    this.dropboxAuthUrl = dbx.getAuthenticationUrl(this.REDIRECT_URI);

    if (this.user.dropboxToken && this.user.dropboxToken !== "undefined") {
      this.isDropboxConnected = true;
    } else {
      let dropboxToken = DropboxUtils.getAccessTokenFromUrl();
      this.isDropboxConnected = dropboxToken && dropboxToken !== "undefined";

      if (this.isDropboxConnected && dropboxToken != this.user.dropboxToken) {
        this.user.dropboxToken = dropboxToken;
        this.updateUserAndGlobalInfo();
      }
    }

    this.isNextcloudConnected = await this.localRepoNextcloudService.isConnected(
      this.user.nextcloudCredentials
    );

    this.nextcloudLoginForm = this.formBuilder.group({
      domain: new FormControl("", Validators.required),
      username: new FormControl("", Validators.required),
      password: new FormControl("", Validators.required),
    });
    this.nextcloudLoginError = false;

    this.location.replaceState("/main/profile");
    this.isLoaded = true;
    this.isJsonServerConnected = await this.localRepoJsonServerService.isConnected();
  }

  async localRepositoryLocationChange(newLocation: LocalRepositoryLocation) {
    if (this.user.localRepositoryLocation != newLocation) {
      let sourceService = this.getService(this.user.localRepositoryLocation);
      let destService = this.getService(newLocation);

      if (sourceService == null) {
        // no location set yet
        this.user.localRepositoryLocation = newLocation;
        this.updateUserAndGlobalInfo();
      } else {
        if (sourceService && destService) {
          try {
            let classInstances = <ClassInstance[]>(
              await sourceService
                .findClassInstancesByVolunteer(this.user)
                .toPromise()
            );

            await destService
              .overrideClassInstances(this.user, classInstances)
              .toPromise();
          } catch (error) {
            alert("Fehler bei der Datenübertragung!");
          }

          this.user.localRepositoryLocation = newLocation;
          this.updateUserAndGlobalInfo();
        } else {
          alert("Fehler: Service nicht verfügbar!");
        }
      }
    }
  }

  private getService(localRepositoryLocation: LocalRepositoryLocation) {
    switch (localRepositoryLocation) {
      case LocalRepositoryLocation.LOCAL:
        return this.isJsonServerConnected
          ? this.localRepoJsonServerService
          : false;

      case LocalRepositoryLocation.DROPBOX:
        return this.isDropboxConnected ? this.localRepoDropboxService : false;

      case LocalRepositoryLocation.NEXTCLOUD:
        return this.isNextcloudConnected
          ? this.localRepoNextcloudService
          : false;
    }
  }

  async loginNextcloud() {
    if (this.nextcloudLoginForm.valid) {
      let nextcloudCredentials: NextcloudCredentials = new NextcloudCredentials(
        this.nextcloudLoginForm.value.domain,
        this.nextcloudLoginForm.value.username,
        this.nextcloudLoginForm.value.password
      );

      let valid: boolean = await this.localRepoNextcloudService.isConnected(
        nextcloudCredentials
      );

      if (valid) {
        this.user.nextcloudCredentials = nextcloudCredentials;
        this.updateUserAndGlobalInfo();
        this.isNextcloudConnected = true;
      } else {
        this.nextcloudLoginError = true;
      }
    }
  }

  async removeNextcloud() {
    this.user.nextcloudCredentials = null;
    this.updateUserAndGlobalInfo();
    this.isNextcloudConnected = false;
  }

  removeDropbox() {
    this.user.dropboxToken = null;
    this.updateUserAndGlobalInfo();
    this.isDropboxConnected = false;
  }

  getChecked(localRepositoryLocation) {
    return this.user.localRepositoryLocation == localRepositoryLocation
      ? true
      : false;
  }

  async updateUserAndGlobalInfo() {
    await this.coreUserService.updateUser(this.user, true).toPromise();

    this.loginService.generateGlobalInfo(
      this.globalInfo.userRole,
      this.globalInfo.tenants.map((t) => t.id)
    );
  }
}
