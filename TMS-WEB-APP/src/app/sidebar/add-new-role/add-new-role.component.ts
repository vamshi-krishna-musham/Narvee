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
  selector: 'app-add-new-role',
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
  templateUrl: './add-new-role.component.html',
  styleUrl: './add-new-role.component.scss'
})
export class AddNewRoleComponent {
  RoleForm: any = FormGroup;
  private snackBarServ = inject(SnackbarService);

  constructor(private fb: FormBuilder, private service:RoleService ,  private dialogRef: MatDialogRef<AddNewRoleComponent>,@Inject(MAT_DIALOG_DATA) public data: any
  ){
   
console.log(data,'dataaaa');

    const profileId = localStorage.getItem('profileId');
    const adminId = localStorage.getItem('adminId');
    const finalAdminId = adminId ? adminId : profileId;
    this.RoleForm = this.fb.group({
      rolename: ['', [Validators.required, this.NameValidator,Validators.minLength(2),
        Validators.maxLength(50)]],
      description: ['', [Validators.required, this.NameValidator,Validators.minLength(2)]],
      adminId: [finalAdminId],
      addedby: [profileId],
      updatedby:['']
    });
  if (this.data) {
  this.RoleForm.patchValue({
    rolename: this.toTitleCase(this.data.rolename),
    description: this.data.roledescription
  });
}

  }
  toTitleCase(str: string): string {
  return str.replace(/\w\S*/g, txt => 
    txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase()
  );
}

noLeadingTrailingOrOnlyWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value || '';

  if (value.trim() === '') {
    return { whitespace: true }; // only whitespace
  }

  // Check for leading/trailing spaces
  if (value !== value.trim()) {
    return { whitespace: true };
  }

  // Only allow letters and spaces between words
  const regex = /^[A-Za-z]+( [A-Za-z]+)*$/;
  return regex.test(value) ? null : { invalidChars: true };
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
  get isEditMode(): boolean {
    return !!this.data?.roleid;
  }
  
  onSubmitTask() {
    if (this.isEditMode) {
      this.updateTaskdetails();
    } else {
      this.onSave();
    }
  }
  
  onCancel() {
    this.dialogRef.close();
  }
isLoading:any
onSave() {
  if (this.RoleForm.invalid) {
    this.RoleForm.markAllAsTouched();

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

  this.isLoading = true;  // START loader

  this.service.createRole(this.RoleForm.value).subscribe({
    next: (res: any) => {
      this.isLoading = false;  // STOP loader

      const isSuccess = res.status === 'success';

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
      this.isLoading = false;  // STOP loader

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

    

updateTaskdetails() {
  if (this.RoleForm.invalid) {
    this.RoleForm.markAllAsTouched();
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

  const payload = {
    ...this.RoleForm.value,
    updatedby: localStorage.getItem('profileId'),
    roleid: this.data?.roleid // only attach if editing
  };

  this.isLoading = true;  // Start loader

  this.service.updateRole(payload).subscribe({
    next: (res: any) => {
      this.isLoading = false;  // Stop loader

      const isSuccess = res.status === 'success';
      const dataToBeSentToSnackBar: ISnackBarData = {
        message: res.message || (isSuccess ? 'Role updated successfully!' : 'Update failed.'),
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
