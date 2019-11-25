import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material";
import { Configurator } from 'app/main/content/_model/meta/Configurator';
import { ConfiguratorService } from 'app/main/content/_service/meta/core/configurator/configurator.service';
import { Marketplace } from 'app/main/content/_model/marketplace';

export interface OpenDialogData {
  configurator: Configurator;
  marketplace: Marketplace;
}

@Component({
  selector: 'open-dialog',
  templateUrl: './open-dialog.component.html',
  styleUrls:['./open-dialog.component.scss']
})
export class OpenDialogComponent implements OnInit{
  
  constructor(
    public dialogRef: MatDialogRef<OpenDialogComponent>, 
    @Inject(MAT_DIALOG_DATA) public data: OpenDialogData,
    private configuratorService: ConfiguratorService,
    ) {
  }

  selected: string;
  configurators: Configurator[];
  recentConfigurators: Configurator[];
  loaded: boolean = false;
  
  ngOnInit() {    
    this.configuratorService.getAllConfiguratorsSortedDesc(this.data.marketplace).toPromise().then((configurators: Configurator[]) => {
      this.configurators = configurators;
      this.recentConfigurators = this.configurators.slice(0, 5);

      this.loaded = true;
    });
  }

  itemSelected(event: any, c: Configurator) {
    this.data.configurator = c;
    this.dialogRef.close(this.data)
    

  }

  onNoClick(): void {
    this.dialogRef.close();
  }






  
}


