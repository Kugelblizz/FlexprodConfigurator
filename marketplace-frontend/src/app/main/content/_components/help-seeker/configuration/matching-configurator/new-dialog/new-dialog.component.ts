import { Component, Inject, OnInit } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Marketplace } from "app/main/content/_model/marketplace";
import { isNullOrUndefined } from "util";
import { LoginService } from "app/main/content/_service/login.service";
import { User, UserRole } from "app/main/content/_model/user";
import { MatchingConfigurationService } from "app/main/content/_service/configuration/matching-configuration.service";
import { ClassConfigurationService } from "app/main/content/_service/configuration/class-configuration.service";
import {
  ClassConfiguration,
  MatchingConfiguration,
} from "app/main/content/_model/meta/configurations";
import { ClassBrowseSubDialogData } from "../../class-configurator/_dialogs/browse-sub-dialog/browse-sub-dialog.component";
import { GlobalInfo } from "app/main/content/_model/global-info";
import { Tenant } from "app/main/content/_model/tenant";

export interface NewMatchingDialogData {
  leftClassConfiguration: ClassConfiguration;
  rightClassConfiguration: ClassConfiguration;
  label: string;
  marketplace: Marketplace;
}

@Component({
  selector: "new-matching-dialog",
  templateUrl: "./new-dialog.component.html",
  styleUrls: ["./new-dialog.component.scss"],
})
export class NewMatchingDialogComponent implements OnInit {
  constructor(
    public dialogRef: MatDialogRef<NewMatchingDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: NewMatchingDialogData,
    private classConfigurationService: ClassConfigurationService,
    private matchingConfigurationService: MatchingConfigurationService,
    private loginService: LoginService
  ) {}

  allClassConfigurations: ClassConfiguration[];
  recentClassConfigurations: ClassConfiguration[];
  loaded = false;
  showErrors = false;
  showDuplicateError = false;
  label: string;

  browseMode: boolean;
  browseDialogData: ClassBrowseSubDialogData;

  tenant: Tenant;

  async ngOnInit() {
    let globalInfo = <GlobalInfo>(
      await this.loginService.getGlobalInfo().toPromise()
    );
    this.tenant = globalInfo.tenants[0];

    this.classConfigurationService
      .getClassConfigurationsByTenantId(this.data.marketplace, this.tenant.id)
      .toPromise()
      .then((classConfigurations: ClassConfiguration[]) => {
        this.recentClassConfigurations = classConfigurations;
        this.allClassConfigurations = classConfigurations;

        //----DEBUG
        // this.recentMatchingConfigurations.push(...this.recentMatchingConfigurations);
        // this.recentMatchingConfigurations.push(...this.recentMatchingConfigurations);
        //----
        this.recentClassConfigurations = this.recentClassConfigurations.sort(
          (a, b) => b.timestamp.valueOf() - a.timestamp.valueOf()
        );

        if (this.recentClassConfigurations.length > 4) {
          this.recentClassConfigurations = this.recentClassConfigurations.slice(
            0,
            4
          );
        }

        this.loaded = true;
      });
  }

  leftItemSelected(event: any, c: ClassConfiguration) {
    this.data.leftClassConfiguration = c;
  }

  rightItemSelected(event: any, c: ClassConfiguration) {
    this.data.rightClassConfiguration = c;
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onOKClick() {
    this.showDuplicateError = false;
    if (
      !isNullOrUndefined(this.data.leftClassConfiguration) &&
      !isNullOrUndefined(this.data.rightClassConfiguration) &&
      this.data.rightClassConfiguration !== this.data.leftClassConfiguration &&
      !isNullOrUndefined(this.data.label)
    ) {
      this.matchingConfigurationService
        .getMatchingConfigurationByUnorderedClassConfigurationIds(
          this.data.marketplace,
          this.data.leftClassConfiguration.id,
          this.data.rightClassConfiguration.id
        )
        .toPromise()
        .then((ret: MatchingConfiguration) => {
          if (isNullOrUndefined(ret)) {
            this.dialogRef.close(this.data);
          } else {
            this.showDuplicateError = true;
            this.showErrors = true;
          }
        });
    } else {
      this.showErrors = true;
    }
  }

  handleBrowseClick(sourceReference: "LEFT" | "RIGHT") {
    this.browseDialogData = new ClassBrowseSubDialogData();
    this.browseDialogData.title = "Durchsuchen";
    this.browseDialogData.marketplace = this.data.marketplace;
    this.browseDialogData.sourceReference = sourceReference;

    this.browseDialogData.entries = [];
    for (const classConfiguration of this.allClassConfigurations) {
      this.browseDialogData.entries.push({
        id: classConfiguration.id,
        name: classConfiguration.name,
        date: new Date(classConfiguration.timestamp),
      });
    }

    this.browseMode = true;
  }

  handleBrowseBackClick() {
    this.browseMode = false;
  }

  handleReturnFromBrowse(event: {
    cancelled: boolean;
    entryId: string;
    sourceReference: "LEFT" | "RIGHT";
  }) {
    console.log("clicked browse");
    console.log(event);

    if (!event.cancelled) {
      const classConfiguration = this.allClassConfigurations.find(
        (c) => c.id === event.entryId
      );

      if (event.sourceReference === "LEFT") {
        this.data.leftClassConfiguration = classConfiguration;
      } else if (event.sourceReference === "RIGHT") {
        this.data.rightClassConfiguration = classConfiguration;
      }
    }

    this.browseMode = false;
  }
}
