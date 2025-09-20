import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators, FormControl, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MatRippleModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { IDropdownSettings, NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { Project } from '../sidenav/project-model';
import { ProjectsService } from '../../services/projects.service';
import { ISnackBarData, SnackbarService } from '../../PathService/snack-bar.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject, Observable, startWith, map, takeUntil } from 'rxjs';
import { TaskService } from '../../PathService/task.service';
import { RoleService } from '../../services/role.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-add-privilege',
    standalone: true,
      imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatAutocompleteModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatSelectModule,
        MatCardModule,
        MatRippleModule,
        NgMultiSelectDropDownModule,
        MatProgressSpinnerModule
      ],
  templateUrl: './add-privilege.component.html',
  styleUrl: './add-privilege.component.scss'
})
export class AddPrivilegeComponent {
  privilegeForm: any = FormGroup;
  private snackBarServ = inject(SnackbarService);

  constructor(private fb: FormBuilder,private service:RoleService,    private dialogRef: MatDialogRef<AddPrivilegeComponent>,@Inject(MAT_DIALOG_DATA) public data: any
  ){
   

    this.privilegeForm = this.fb.group({
      type: ['',[Validators.required, this.NameValidator,Validators.minLength(2),
        Validators.maxLength(50)]],
      name: ['', [Validators.required, this.NameValidator ,Validators.minLength(2),
        Validators.maxLength(50)]],
      cardType :['',[Validators.required, this.NameValidator ,Validators.minLength(2),
        Validators.maxLength(50)]]
    });
    
  }

      NameValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';

    if (value.trim() === '') {
      return { whitespace: true };
    }

    // Allow alphanumeric and special chars, but must contain at least one letter
    const hasLetter = /[a-zA-Z]/.test(value);
    if (!hasLetter) {
      return { noLetter: true };
    }

    return null; // valid
  }

  onCancel() {
    this.dialogRef.close();
  }
isLoading = false;  // Add this to your component class properties

onSave() {
  if (this.privilegeForm.invalid) {
    this.privilegeForm.markAllAsTouched();

    const dataToBeSentToSnackBar: ISnackBarData = {
      message: 'Please fill all required fields correctly.',  
      duration: 2500,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      direction: 'above',
      panelClass: ['custom-snack-failure'],  
    };

    this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
    return;  
  }

  this.isLoading = true;  // Start loader

  this.service.saveprivilege(this.privilegeForm.value).subscribe({
    next: (res: any) => {
      this.isLoading = false;  // Stop loader

      const isSuccess = res.status === 'Success';

      const dataToBeSentToSnackBar: ISnackBarData = {
        message: res.message || (isSuccess ? 'Team member added successfully!' : 'Registration failed.'),
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure'],
      };

      this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);

      if (isSuccess) {
        this.dialogRef.close(true);
      }
    },
    error: (err: any) => {
      this.isLoading = false;  // Stop loader

      const dataToBeSentToSnackBar: ISnackBarData = {
        message: err?.error?.message || 'Something went wrong. Please try again.',
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure'],
      };
      this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
    }
  });
}

}
