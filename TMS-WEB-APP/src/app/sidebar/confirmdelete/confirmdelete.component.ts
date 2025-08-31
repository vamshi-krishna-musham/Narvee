import { Component, Inject, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { IConfirmDialogData } from '../sidenav/projects/confirm-dialog-data';

@Component({
  selector: 'app-confirmdelete',
  templateUrl: './confirmdelete.component.html',
  styleUrl: './confirmdelete.component.scss'
})
export class ConfirmdeleteComponent {
  private dialog = inject(MatDialog);
  allowAction: boolean = false;
  constructor(@Inject(MAT_DIALOG_DATA) protected data: IConfirmDialogData,
  public dialogRef: MatDialogRef<ConfirmdeleteComponent>){

  }

  onAction(action: string){

    // if(["SAFE_CLOSE", "NO"].includes(action)){
    //   this.dialog.closeAll();
    // }

    if(action === "SAFE_CLOSE"){
      this.dialogRef.close()
    }

    if(action === "NO"){
      this.dialogRef.close()
    }

    if(action === "YES"){
      // call delete apis
      this.allowAction = true;
      this.dialogRef.close()
    }

  }
}
