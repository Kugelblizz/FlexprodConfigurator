import {Component, Input, OnInit} from '@angular/core';

import {Task} from '../../../../_model/task';
import {Marketplace} from '../../../../_model/marketplace';

import {TaskService} from '../../../../_service/task.service';
import {CoreMarketplaceService} from '../../../../_service/core-marketplace.service';

@Component({
  selector: 'fuse-project-task-list',
  templateUrl: './project-task-list.component.html',
  styleUrls: ['./project-task-list.component.scss']
})
export class FuseProjectTaskListComponent implements OnInit {


  @Input('projectId')
  private projectId: string;
  @Input('marketplaceId')
  public marketplaceId: string;

  public tasks: Array<Task>;

  constructor(private taskService: TaskService, private marketplaceService: CoreMarketplaceService) {
  }

  ngOnInit() {
    this.marketplaceService.findById(this.marketplaceId).toPromise().then((marketplace: Marketplace) => {
      this.taskService.findAllByProjectId(marketplace, this.projectId).toPromise().then((tasks: Array<Task>) => this.tasks = tasks);
    });
  }


}
