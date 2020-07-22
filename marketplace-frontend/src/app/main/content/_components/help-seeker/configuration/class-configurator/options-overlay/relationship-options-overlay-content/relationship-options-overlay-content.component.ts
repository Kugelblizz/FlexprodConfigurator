
import { isNullOrUndefined } from "util";
import { Marketplace } from 'app/main/content/_model/marketplace';
import { User } from 'app/main/content/_model/user';
import { ClassDefinition } from 'app/main/content/_model/meta/class';
import { Relationship, RelationshipType } from 'app/main/content/_model/meta/relationship';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { PropertyOrEnumEntry } from '../class-options-overlay-content/class-options-overlay-content.component';
import { CConstants } from '../../utils-and-constants';
import { OptionsOverlayContentData } from '../options-overlay-control/options-overlay-control.component';



@Component({
  selector: "relationship-options-overlay-content",
  templateUrl: "./relationship-options-overlay-content.component.html",
  styleUrls: ["./relationship-options-overlay-content.component.scss"],
})
export class RelationshipOptionsOverlayContentComponent implements OnInit {
  @Input() inputData: OptionsOverlayContentData;
  @Output() resultData = new EventEmitter<OptionsOverlayContentData>();

  relationshipPalettes = CConstants.relationshipPalettes;

  entryList: PropertyOrEnumEntry[];

  constructor(
  ) { }

  ngOnInit() {
  }

  onSubmit() {
    this.resultData.emit(this.inputData);
  }

  onCancel() {
    this.resultData.emit(undefined);
  }

  getImagePathForRelationship(relationshipType: RelationshipType) {
    return this.relationshipPalettes.rows.find((r) => r.id === relationshipType)
      .imgPath;
  }

  getLabelForRelationship(relationshipType: RelationshipType) {
    return RelationshipType.getLabelFromRelationshipType(relationshipType);
  }

  onRelationshipTypeSelect(relationshipType: RelationshipType) {
    console.log(relationshipType);
    this.inputData.relationship.relationshipType = relationshipType;
  }

}
