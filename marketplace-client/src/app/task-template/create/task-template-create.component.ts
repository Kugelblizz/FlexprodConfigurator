import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {TaskTemplateService} from '../../_service/task-template.service';
import {TaskTemplate} from '../../_model/task-template';
import {ActivatedRoute, Router} from '@angular/router';
import {TaskTemplateValidator} from '../task-template.validator';
import {isNullOrUndefined} from 'util';
import {CompetenceService} from '../../_service/competence.service';
import {Competence} from '../../_model/competence';

@Component({
  templateUrl: './task-template-create.component.html',
  styleUrls: ['./task-template-create.component.css']
})
export class TaskTemplateCreateComponent implements OnInit {
  competences: Competence[];
  taskTemplateForm: FormGroup;

  constructor(formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private taskTemplateService: TaskTemplateService,
              private competenceService: CompetenceService) {
    this.taskTemplateForm = formBuilder.group({
      'id': new FormControl(undefined),
      'name': new FormControl(undefined, Validators.required),
      'description': new FormControl(undefined, Validators.required),
      'requiredCompetences': new FormControl([]),
      'acquirableCompetences': new FormControl([])
    }, {validator: TaskTemplateValidator});
  }


  ngOnInit() {
    this.competenceService.findAll().toPromise().then((competences: Competence[]) => {
      this.competences = competences;
      this.route.params.subscribe(params => this.findTaskTemplate(params['id']));
    });
  }

  private findTaskTemplate(id: string) {
    if (isNullOrUndefined(id) || id.length === 0) {
      return;
    }
    this.taskTemplateService.findById(id).toPromise().then((taskTemplate: TaskTemplate) => {
      this.taskTemplateForm.setValue({
        id: taskTemplate.id,
        name: taskTemplate.name,
        description: taskTemplate.description,
        requiredCompetences: this.competences.filter((competence: Competence) => {
          return taskTemplate.requiredCompetences.find((requiredCompetence: Competence) => requiredCompetence.name === competence.name);
        }),
        acquirableCompetences: this.competences.filter((competence: Competence) => {
          return taskTemplate.acquirableCompetences.find((acquirableCompetence: Competence) => acquirableCompetence.name === competence.name);
        })
      });
    });
  }

  save() {
    if (!this.taskTemplateForm.valid) {
      return;
    }
    this.taskTemplateService.save(<TaskTemplate> this.taskTemplateForm.value)
      .toPromise()
      .then((taskTemplate: TaskTemplate) => this.router.navigate(['/taskTemplates']));
  }
}
