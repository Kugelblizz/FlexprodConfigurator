import {Component, OnInit} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import {LoginService} from '../_service/login.service';
import {CoreEmployeeService} from '../_service/core-employee.service';
import {Participant} from '../_model/participant';
import {Marketplace} from '../_model/marketplace';
import {Project} from '../_model/project';
import {ProjectService} from '../_service/project.service';

@Component({
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss']
})
export class FuseProjectListComponent implements OnInit {

  dataSource = new MatTableDataSource<Project>();
  displayedColumns = ['name', 'description', 'startDate', 'endDate', 'actions'];

  constructor(private loginService: LoginService,
              private coreEmployeeService: CoreEmployeeService,
              private projectService: ProjectService) {
  }

  ngOnInit() {
    this.loginService.getLoggedIn().toPromise().then((employee: Participant) => {
      this.coreEmployeeService.findRegisteredMarketplaces(employee.id).toPromise().then((marketplace: Marketplace) => {
        this.projectService.findAll(marketplace)
          .toPromise()
          .then((projects: Project[]) => this.dataSource.data = projects);
      });
    });
  }
}