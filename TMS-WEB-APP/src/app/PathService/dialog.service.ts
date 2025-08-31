import { ComponentType } from '@angular/cdk/portal';
import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';

@Injectable({
  providedIn: 'root'
})
export class DialogService {
  constructor(private dialog:  MatDialog) { }

  openDialogWithComponent(comp: ComponentType<any> , dialogConfig: MatDialogConfig){
    dialogConfig.disableClose = true;
    return this.dialog.open(comp, dialogConfig);

  }}
