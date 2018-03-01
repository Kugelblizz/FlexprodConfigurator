import {NgModule} from '@angular/core';
import {AppMaterialModule} from './app-material.module';
import {AppRoutingModule} from './app-routing.module';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';
import {ReactiveFormsModule} from '@angular/forms';

import {AppComponent} from './app.component';

import {TaskService} from './task/task.service';
import {TaskTypeService} from './task-type/task-type.service';
import {TaskCreateComponent} from './task/create/task-create.component';
import {ReserveTaskComponent} from './reserve-task/reserve-task.component';
import {TaskTransactionService} from './task-transaction/task-transaction.service';
import {VolunteerService} from './participant/volunteer.service';
import {TaskListComponent} from './task/list/task-list.component';
import {TaskTypeListComponent} from './task-type/list/task-type-list.component';
import {TaskTypeCreateComponent} from './task-type/create/task-type-create.component';

import {NavbarComponent} from './navbar/navbar.component';
import {TaskDetailsComponent} from './task/details/task-details.component';


@NgModule({
  declarations: [
    AppComponent,
    ReserveTaskComponent,
    NavbarComponent,
    TaskListComponent,
    TaskCreateComponent,
    TaskDetailsComponent,
    TaskTypeListComponent,
    TaskTypeCreateComponent
  ],
  imports: [
    AppMaterialModule,
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [
    TaskService,
    TaskTypeService,
    TaskTransactionService,
    VolunteerService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
