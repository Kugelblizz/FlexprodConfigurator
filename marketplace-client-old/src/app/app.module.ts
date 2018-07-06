import {NgModule} from '@angular/core';
import {AppMaterialModule} from './app-material.module';
import {AppRoutingModule} from './app-routing.module';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ReactiveFormsModule} from '@angular/forms';
import {AppComponent} from './app/app.component';
import {NavbarComponent} from './navbar/navbar.component';
import {LoginComponent} from './login/login.component';
import {TaskListComponent} from './task/list/task-list.component';
import {TaskAvailableComponent} from './task/available/task-available.component';
import {TaskCreateComponent} from './task/create/task-create.component';
import {TaskDetailComponent} from './task/detail/task-detail.component';
import {TaskTemplateListComponent} from './task-template/list/task-template-list.component';
import {TaskTemplateCreateComponent} from './task-template/create/task-template-create.component';
import {TaskAssignComponent} from './task/assign/task-assign.component';
import {TaskInteractionHistoryComponent} from './task-interaction/history/task-interaction-history.component';
import {VolunteerProfileComponent} from './volunteer/profile/volunteer-profile.component';
import {TokenGuard} from './_guard/token.guard';
import {ArrayService} from './_service/array.service';
import {LoginService} from './_service/login.service';
import {MessageService} from './_service/message.service';
import {CompetenceService} from './_service/competence.service';
import {SourceService} from './_service/source.service';
import {ContractorService} from './_service/contractor.service';
import {TaskService} from './_service/task.service';
import {TaskTemplateService} from './_service/task-template.service';
import {TaskInteractionService} from './_service/task-interaction.service';
import {EmployeeGuard} from './_guard/employee.guard';
import {EmployeeService} from './_service/employee.service';
import {VolunteerGuard} from './_guard/volunteer.guard';
import {VolunteerService} from './_service/volunteer.service';
import {VolunteerProfileService} from './_service/volunteer-profile.service';
import {VolunteerRepositoryService} from './_service/volunteer-repository.service';
import {GermanDateAdapter} from './_adapter/german-date-adapter';
import {TokenInterceptor} from './_interceptor/token.interceptor';
import {Http401Interceptor} from './_interceptor/http-401.interceptor';
import {DateAdapter} from '@angular/material/core';
import {WorkflowService} from './_service/workflow.service';
import {TaskTreeComponent} from './task/task-tree/task-tree.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    LoginComponent,
    TaskListComponent,
    TaskAvailableComponent,
    TaskCreateComponent,
    TaskDetailComponent,
    TaskTemplateListComponent,
    TaskTemplateCreateComponent,
    TaskAssignComponent,
    TaskInteractionHistoryComponent,
    VolunteerProfileComponent,
    TaskTreeComponent
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
    TokenGuard,
    ArrayService,
    LoginService,
    MessageService,
    CompetenceService,
    SourceService,
    ContractorService,
    TaskService,
    TaskTemplateService,
    TaskInteractionService,
    EmployeeGuard,
    EmployeeService,
    VolunteerGuard,
    VolunteerService,
    VolunteerProfileService,
    VolunteerRepositoryService,
    WorkflowService,
    {provide: DateAdapter, useClass: GermanDateAdapter},
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: Http401Interceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
