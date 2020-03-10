import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { UserDefinedTaskTemplate } from 'app/main/content/_model/user-defined-task-template';
import { templateNameUniqueValidator } from 'app/main/content/_validator/template-name-unique.validator';
import { UserDefinedTaskTemplateService } from 'app/main/content/_service/user-defined-task-template.service';
import { LoginService } from 'app/main/content/_service/login.service';
import { CoreHelpSeekerService } from 'app/main/content/_service/core-helpseeker.service';
import { Participant } from 'app/main/content/_model/participant';
import { isNullOrUndefined } from 'util';
import { Marketplace } from 'app/main/content/_model/marketplace';

export interface ChooseTemplateToCopyDialogData {
  entries: {id: string, label: string, description: string}[];
  returnId: string;
  newLabel: string;
  newDescription: string;
}

@Component({
  selector: 'app-choose-dialog',
  templateUrl: './choose-dialog.component.html',
  styleUrls: ['./choose-dialog.component.scss']
})
export class ChooseTemplateToCopyDialogComponent implements OnInit {

  firstFormGroup: FormGroup;

  templateNames: string[];

  step1Loaded: boolean;

  constructor(
    public dialogRef: MatDialogRef<ChooseTemplateToCopyDialogData>, @Inject(MAT_DIALOG_DATA)
    public data: ChooseTemplateToCopyDialogData,
    private formBuilder: FormBuilder,

    private userDefinedTaskTemplateService: UserDefinedTaskTemplateService,
    private loginService: LoginService,
    private helpSeekerService: CoreHelpSeekerService,


    // public returnData: ChooseDialogReturnData
    ) {
  }

  displayedColumns: string[] = ['label', 'description', 'option'];


  ngOnInit() {
    this.step1Loaded = false;
    this.getTemplateNames();
 
  }


  getTemplateNames() {
    console.log("prepare Choose Properties Step for template ");
    this.loginService.getLoggedIn().toPromise().then((participant: Participant) => {
      this.helpSeekerService.findRegisteredMarketplaces(participant.id).toPromise().then((marketplace: Marketplace) => {
        if (!isNullOrUndefined(marketplace)) {
          this.userDefinedTaskTemplateService.getTemplateNames(marketplace).toPromise().then((templateNames: string[]) => {
            this.templateNames = templateNames;
          }).then(() => {
            this.createStep1Form();
          });
        }
      });
    }); 
  }

  createStep1Form() {

    this.firstFormGroup = this.formBuilder.group({
      name: ['', [Validators.required, templateNameUniqueValidator(this.templateNames)] ],
      description: ['']
    });


    this.step1Loaded = true;

  }

  setReturnValues() {
    // console.log(this.firstFormGroup.value);
    this.data.newLabel = this.firstFormGroup.get('name').value;
    this.data.newDescription = this.firstFormGroup.get('description').value;
  }

  getNewName() {

    return !isNullOrUndefined(this.firstFormGroup) ? this.firstFormGroup.get('name').value : '';
  }



  onNoClick(): void {
    this.dialogRef.close();
  }

}