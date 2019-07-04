import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { LoginService } from '../_service/login.service';
import { CoreMarketplaceService } from '../_service/core-marketplace.service';
import { ParticipantRole, Participant } from '../_model/participant';
import { Property } from '../_model/properties/Property';
import { Marketplace } from '../_model/marketplace';
import { PropertyService } from '../_service/property.service';
import { UserDefinedTaskTemplate } from '../_model/user-defined-task-template';
import { UserDefinedTaskTemplateService } from '../_service/user-defined-task-template.service';
import { QuestionService } from '../_service/question.service';
import { QuestionBase } from '../_model/dynamic-forms/questions';
import { isNullOrUndefined, isNull } from 'util';
import { DialogFactoryComponent } from '../_components/dialogs/_dialog-factory/dialog-factory.component';
import { SortDialogData } from '../_components/dialogs/sort-dialog/sort-dialog.component';

@Component({
  selector: 'user-defined-task-template-detail-single',
  templateUrl: './user-defined-task-template-detail-single.html',
  styleUrls: ['./user-defined-task-template-detail-single.scss'],
  providers:  [QuestionService, DialogFactoryComponent]
})
export class SingleUserDefinedTaskTemplateDetailComponent implements OnInit {

  role: ParticipantRole;
  participant: Participant;
  marketplace: Marketplace;
  template: UserDefinedTaskTemplate;
  isLoaded: boolean;
  dialogIds: string[];
  questions: QuestionBase<any>[];
  properties: Property<any>[];
 
  constructor(private router: Router,
    private route: ActivatedRoute,
    private loginService: LoginService,
    private marketplaceService: CoreMarketplaceService,
    private userDefinedTaskTemplateService: UserDefinedTaskTemplateService,
    private questionService: QuestionService,
    private propertyService: PropertyService,
    private dialogFactory: DialogFactoryComponent
    ) {
      this.isLoaded = false;
    }

  ngOnInit() {
    
    Promise.all([
      this.loginService.getLoggedInParticipantRole().toPromise().then((role: ParticipantRole) => this.role = role),
      this.loginService.getLoggedIn().toPromise().then((participant: Participant) => this.participant = participant),
    ]).then(() => {
      this.route.params.subscribe(params => this.loadPropertiesFromTemplate(params['marketplaceId'], params['templateId']));
    })
  }

  loadPropertiesFromTemplate(marketplaceId: string, templateId: string): void {
    this.marketplaceService.findById(marketplaceId).toPromise().then((marketplace: Marketplace) => {
      this.marketplace = marketplace;
      this.userDefinedTaskTemplateService.getTemplate(marketplace, templateId).toPromise().then((template: UserDefinedTaskTemplate) => {
        this.template = template;    
     
      })
      .then(() => {
        this.propertyService.getProperties(this.marketplace).toPromise().then((properties: Property<any>[]) => {
          this.properties = properties;
          console.log("loaded Properties: ")
          console.log(this.properties);
        
        })
        .then(() => {
          if (!isNullOrUndefined(this.template)) {
            this.questions = this.questionService.getQuestionsFromProperties(this.template.properties);
          }
          this.isLoaded = true;
        })
      });
    });  
  }


  loadProperties() {
    this.propertyService.getProperties(this.marketplace).toPromise().then((properties: Property<any>[]) => {
      this.properties = properties;
    });
  }


  navigateBack() {
    window.history.back();
  }

  navigateEditForm() {
    console.log("navigate to edit form");
    this.router.navigate([`/main/task-templates/user/edit/${this.marketplace.id}/${this.template.id}`], {queryParams: {ref: 'single'}});
  }

  deleteTemplate() {
    console.log("clicked delete template");

    this.dialogFactory.confirmationDialog(
      "Are you sure?", "Are you sure you want to delete this Template? This action cannot be reverted")
      .then((cont: boolean) => {
        if (cont) {
          this.userDefinedTaskTemplateService.deleteRootTaskTemplate(this.marketplace, this.template.id).toPromise().then( (success: boolean) => {
            console.log("done - navigate back");
            this.navigateBack();
          });
        }
      });
    }

    

  addPropertyDialog() {
    console.log("clicked add property");

    this.dialogFactory.addPropertyDialog(this.template, this.properties).then((propIds: string[]) => {   
      if (!isNullOrUndefined(propIds)) {
        this.userDefinedTaskTemplateService.addPropertiesToSingleTemplate(this.marketplace, this.template.id, propIds).toPromise().then(() => {
          console.log("service called");
          this.refresh();
        });
      }
    });
  }
  
  removePropertyDialog() {
    console.log("clicked remove properies");

    this.dialogFactory.removePropertyDialog(this.template).then((propIds: string[]) => {
      if (!isNullOrUndefined(propIds)) {
        this.userDefinedTaskTemplateService.removePropertiesFromSingleTemplate(this.marketplace, this.template.id, propIds).toPromise().then(() => {
          this.refresh();
        });
      }
    });
    
  }

  //TODO
  changePropertyOrderDialog() {
    console.log("clicked order properties");

    this.dialogFactory.changePropertyOrderDialog(this.template.properties).then((data: SortDialogData) => {
      if (!isNullOrUndefined(data)) {
        for (let i = 0; i<data.list.length; i++) {
          data.list[i].order = i;
        }

        console.log("Properties after order change");

        console.log(data);
        // this.template.properties = properties;

        this.userDefinedTaskTemplateService.updatePropertyOrderSingle(this.marketplace, this.template.id, data.list).toPromise().then((ret: UserDefinedTaskTemplate) => {
          
          console.log("result");
          console.log(ret);
          this.refresh();
        });

      }  

    });
  }

  editDescriptionDialog() {
    console.log("entered edit Description Dialog");

    this.dialogFactory.editTemplateDescriptionDialog(this.template).then((description: string) => {
      if (!isNullOrUndefined(description)) {
        this.userDefinedTaskTemplateService.updateRootTaskTemplate(this.marketplace, this.template.id, null, description).toPromise().then((updatedTemplate: UserDefinedTaskTemplate) => {
          this.template.description = updatedTemplate.description;
        });
      }
    });
  }
  
  editNameDialog() {
    console.log("Entered edit Name Dialog");

    this.dialogFactory.editTemplateNameDialog(this.template).then((name: string) => {
      if (!isNullOrUndefined(name)) {
        this.userDefinedTaskTemplateService.updateRootTaskTemplate(this.marketplace, this.template.id, name, null).toPromise().then((updatedTemplate: UserDefinedTaskTemplate) => {
          this.template.name = updatedTemplate.name;
        });
      }
    });
  }




  private refresh() {
    this.isLoaded = false;
    this.ngOnInit();
  }
}



