import { Component, inject, Inject } from '@angular/core';
import { IConfirmDialogData } from '../sidenav/projects/confirm-dialog-data';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
@Component({
  selector: 'app-common-delete',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, MatDialogModule],
  templateUrl: './common-delete.component.html',
  styleUrl: './common-delete.component.scss'
})
export class CommonDeleteComponent {
  private dialog = inject(MatDialog);
  allowAction: boolean = false;
  constructor(@Inject(MAT_DIALOG_DATA) protected data: IConfirmDialogData,
  public dialogRef: MatDialogRef<CommonDeleteComponent>){

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
