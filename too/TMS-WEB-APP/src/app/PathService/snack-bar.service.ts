import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { Injectable } from '@angular/core';
import { CustomSnackBarComponent } from '../Components/custom-snack-bar/custom-snack-bar.component';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {
  constructor(private snackBarRef: MatSnackBar) { }

 
  openSnackBarFromComponent(data: ISnackBarData){
    const snackBarConfig = new MatSnackBarConfig();

    snackBarConfig.data = data;
    snackBarConfig.duration = data.duration;
    snackBarConfig.direction = data.direction;
    snackBarConfig.verticalPosition = data.verticalPosition;
    snackBarConfig.horizontalPosition = data.horizontalPosition;
    snackBarConfig.panelClass = data.panelClass
    this.snackBarRef.openFromComponent(CustomSnackBarComponent,snackBarConfig);
  }
}

export interface ISnackBarData {
  message: string;
  actionText?: string;
  duration: number;
  direction?: any;
  horizontalPosition?: any;
  verticalPosition?: any;
  panelClass: string[]
}

