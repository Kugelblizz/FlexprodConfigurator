import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { StoredChartService } from "../../../_service/stored-chart.service";
import { LoginService } from "../../../_service/login.service";
import { Participant } from "../../../_model/participant";
import { CoreMarketplaceService } from "../../../_service/core-marketplace.service";
import { Marketplace } from "../../../_model/marketplace";
import { StoredChart } from "../../../_model/stored-chart";
import { isNullOrUndefined } from "util";
import { ClassInstance, ClassInstanceDTO } from "../../../_model/meta/Class";
import { ClassInstanceService } from "../../../_service/meta/core/class/class-instance.service";
import { MatPaginator, MatSort } from "@angular/material";
import { TenantService } from "../../../_service/core-tenant.service";
import { VolunteerService } from "../../../_service/volunteer.service";
import { Volunteer } from "../../../_model/volunteer";

@Component({
  selector: "recruit-view",
  templateUrl: "./recruit-view.component.html",
  styleUrls: ["./recruit-view.component.scss"]
})
export class RecruitViewComponent implements OnInit, AfterViewInit {
  private tableDataSource = new MatTableDataSource<ClassInstanceDTO>([]);
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  private displayedColumns: string[] = [
    "taskName",
    "taskType1",
    "taskDateFrom",
    "taskDuration",
    "hash",
    "verificationStatus"
  ];
  marketplace: Marketplace;
  participant: Participant;
  duringVerify: boolean;
  verifyStage: number;
  charts: StoredChart[];
  private classInstanceDTOs: ClassInstanceDTO[] = [];

  // chart options
  colorScheme = "cool";
  showXAxis: boolean = true;
  showYAxis: boolean = true;
  gradient: boolean = false;
  showLegend: boolean = true;
  legendTitle: string = " ";
  showXAxisLabel: boolean = false;
  showYAxisLabel: boolean = true;
  yAxisLabel1: string = "Stunden";
  yAxisLabel2: string = "Anzahl";
  animations: boolean = true;

  weekdayData;
  dayNightData;
  trainingData: any[];
  taskData: any[];

  private tenantName: string = "FF Eidenberg";
  private tenantId: string[] = [];

  private volunteer: Volunteer;

  constructor(
    private storedChartService: StoredChartService,
    private loginService: LoginService,
    private marketplaceService: CoreMarketplaceService,
    private classInstanceService: ClassInstanceService,
    private coreTenantService: TenantService,
    private volunteerService: VolunteerService
  ) {}

  ngOnInit() {}

  ngAfterViewInit(): void {
    Promise.all([
      this.marketplaceService
        .findAll()
        .toPromise()
        .then((marketplaces: Marketplace[]) => {
          if (!isNullOrUndefined(marketplaces)) {
            this.marketplace = marketplaces[0];
          }
        }),
      this.loginService
        .getLoggedIn()
        .toPromise()
        .then((participant: Participant) => {
          this.participant = participant;
        })
    ]).then(() => {
      this.loadStoredCharts();
      this.loadTasks();
    });
  }

  private loadTasks() {
    this.coreTenantService
      .findByName(this.tenantName)
      .toPromise()
      .then((tenantId: string) => {
        this.tenantId.push(tenantId);

        this.volunteerService
          .findByName(this.marketplace, "mweixlbaumer")
          .toPromise()
          .then((volunteer: Volunteer) => {
            this.volunteer = volunteer;

            this.classInstanceService
              .getUserClassInstancesByArcheType(
                this.marketplace,
                "TASK",
                this.volunteer.id,
                this.tenantId
              )
              .toPromise()
              .then((ret: ClassInstanceDTO[]) => {
                if (!isNullOrUndefined(ret)) {
                  this.classInstanceDTOs = ret.sort(
                    (a, b) =>
                      b.blockchainDate.valueOf() - a.blockchainDate.valueOf()
                  );

                  this.tableDataSource.data = this.classInstanceDTOs;
                  this.paginator.length = this.classInstanceDTOs.length;
                  this.tableDataSource.paginator = this.paginator;
                  // this.tableDataSource.paginator.length= this.classInstances.length;
                }
              });
          });
      });
  }

  private loadStoredCharts() {
    this.storedChartService
      .findAll(this.marketplace)
      .toPromise()
      .then((storedCharts: StoredChart[]) => {
        this.charts = storedCharts;
        if (this.charts.findIndex(c => c.title === "Wochentag") >= 0) {
          this.weekdayData = JSON.parse(
            this.charts.find(c => c.title === "Wochentag").data
          );
        }
        if (this.charts.findIndex(c => c.title == "Tageszeit") >= 0) {
          this.dayNightData = JSON.parse(
            this.charts.find(c => c.title === "Tageszeit").data
          );
        }
        if (
          this.charts.findIndex(
            c => c.title == "STUNDEN absolvierter Ausbildungen"
          ) >= 0
        ) {
          this.trainingData = JSON.parse(
            this.charts.find(
              c => c.title == "STUNDEN absolvierter Ausbildungen"
            ).data
          );
        }
        if (
          this.charts.findIndex(
            c =>
              c.title ==
              "Engagement in verschiedenen Tätigkeitsarten im Zeitverlauf"
          ) >= 0
        ) {
          this.taskData = JSON.parse(
            this.charts.find(
              c =>
                c.title ==
                "Engagement in verschiedenen Tätigkeitsarten im Zeitverlauf"
            ).data
          );
        }
        // Engagement in verschiedenen Tätigkeitsarten im Zeitverlauf
      });
  }

  verify1 = false;
  verify2 = false;
  verify3 = false;
  verify5 = false;

  pressedVerifyAll() {
    const outer = this;
    setTimeout(function() {
      outer.verify2 = true;
    }, 3000);
    setTimeout(function() {
      outer.verify3 = true;
    }, 5000);
    setTimeout(function() {
      outer.verify5 = true;
    }, 1000);
    setTimeout(function() {
      outer.verify1 = true;
    }, 2000);
  }

  getVerifyState(index: number) {
    if (index % 2 === 0) {
      return this.verify2;
    } else if (index % 3 === 0) {
      return this.verify3;
    } else if (index % 5 === 0) {
      return this.verify5;
    } else {
      return this.verify1;
    }
  }
}
