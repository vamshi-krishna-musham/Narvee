import { Component, Inject, inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TaskmanagementService } from '../../../services/taskmanagement.service';
import { ISnackBarData, SnackbarService } from '../../../PathService/snack-bar.service';
import { RoleService } from '../../../services/role.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { MatDialogModule } from '@angular/material/dialog';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatNativeDateModule } from '@angular/material/core'; // Import MatNativeDateModule
import { NgxMatIntlTelInputComponent } from 'ngx-mat-intl-tel-input';
import parsePhoneNumberFromString from 'libphonenumber-js';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-add-team-member',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    CommonModule,
    MatTabsModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatCardModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    RouterModule,
    MatDialogModule,
    ReactiveFormsModule,
    FormsModule,
    MatNativeDateModule,
    MatCheckboxModule,
    NgxMatIntlTelInputComponent,
    MatProgressSpinnerModule

  ],
  templateUrl: './add-team-member.component.html',
  styleUrl: './add-team-member.component.scss'
})
export class AddTeamMemberComponent {
  teammember: FormGroup;
  isEditMode: any

  constructor(
    private dialogRef: MatDialogRef<AddTeamMemberComponent>,
    private fb: FormBuilder,
    private service: TaskmanagementService,
    private roleservice: RoleService,
    @Inject(MAT_DIALOG_DATA) public editData: any, // <-- receive the passed data

  ) {
    const profileId = localStorage.getItem('profileId');
    const adminId = localStorage.getItem('adminId');
    const finalAdminId = adminId ? adminId : profileId;

    this.teammember = this.fb.group({
      firstName: ['',[Validators.required, this.noLeadingTrailingOrOnlyWhitespaceValidator , Validators.minLength(2),
    Validators.maxLength(30),]],
      middleName: ['',[this.allowMiddleName , Validators.minLength(2),
    Validators.maxLength(30),]],
      lastName: ['', [Validators.required, this.noLeadingTrailingOrOnlyWhitespaceValidator, Validators.minLength(2),
    Validators.maxLength(30),]],
email: [
  '',
  [
    Validators.required,
    Validators.email,
    Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z]{2,}\\.[a-zA-Z]{2,}$')
  ]
],      contactNumber: ['', [Validators.required]],
      position: ['', [Validators.required, this.NameValidator,Validators.minLength(2),
    Validators.maxLength(30)]],
      adminId: [finalAdminId],
      addedBy: [profileId],
      roleId: ['', Validators.required], // <-- ADD THIS

    });

    console.log(editData, 'editData');

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
allowMiddleName(control: AbstractControl): ValidationErrors | null {
  const value = control.value || '';

  // Allow empty (optional field)
  if (value === '') {
    return null;
  }

  // Disallow only whitespace
  if (value.trim() === '') {
    return { whitespace: true };
  }

  // Disallow leading/trailing spaces
  if (value !== value.trim()) {
    return { whitespace: true };
  }

  // Only allow letters and single spaces between words
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

  roleList: any
  ngOnInit() {
    this.getRoles().then(() => {
      if (this.editData?.userId) {
        this.isEditMode = true;
        this.TeamMembergetById(this.editData.userId);
      }
    });
  }

  TeamMembergetById(userId: number): void {
    this.service.TeamMemberGetId(userId).subscribe((res: any) => {
      console.log(res, 'res');

      // Wait until roleList is loaded before patching roleId
      const roleNameFromApi = res.data.role?.rolename;

      const selectedRole = this.roleList.find((role: any) => role.roleName === roleNameFromApi);

      this.teammember.patchValue({
        firstName: res.data.firstName,
        middleName: res.data.middleName,
        lastName: res.data.lastName,
        email: res.data.email,
        contactNumber: res.data.contactNumber,
        position: res.data.position,
        roleId: selectedRole?.roleid || '' // patch matched roleid or empty
      });
    });
  }




  onlyNumberKey(evt: any) {
    var ASCIICode = (evt.which) ? evt.which : evt.keyCode
    if (ASCIICode > 31 && (ASCIICode < 48 || ASCIICode > 57))
      return false;
    return true;
  }

  onCancel() {
    this.dialogRef.close();
  }
  private snackBarServ = inject(SnackbarService);
  isLoading: any
  onSubmitTask() {
    if (this.isEditMode) {
      this.updateTeamMemberdetails();
    } else {
      this.onSave();
    }
  }
  onSave() {
    if (this.teammember.invalid) {
      this.teammember.markAllAsTouched();
      this.snackBarServ.openSnackBarFromComponent({
        message: 'Please fill all required fields correctly.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        direction: 'above',
        panelClass: ['custom-snack-failure'],
      });
      return;
    }

    this.isLoading = true;  // Start loader

    const rawPhone = this.teammember.get('contactNumber')?.value;
    const parsedPhone = parsePhoneNumberFromString(rawPhone);

    let formattedPhone = rawPhone;
    if (parsedPhone) {
      formattedPhone = `+${parsedPhone.countryCallingCode} ${parsedPhone.nationalNumber}`;
    }
 const email = this.teammember.get('email')?.value?.toLowerCase();
  this.teammember.get('email')?.setValue(email); // update the form control if needed
    const payload: any = {
      ...this.teammember.value,
      contactNumber: formattedPhone,
    };

    if (this.isEditMode) {
      payload.userId = this.editData.userId;
      payload.updatedBy = localStorage.getItem('profileId'); // or adminId
    }

    this.service.managementregister(payload).subscribe({
      next: (res: any) => {
        this.isLoading = false;  // Stop loader

        const dataToBeSentToSnackBar: ISnackBarData = {
          message: '',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: [],
        };

        if (res.status === 'success') {
          dataToBeSentToSnackBar.message =
            res.message || (this.isEditMode ? 'Team member updated successfully!' : 'Team member added successfully!');
          dataToBeSentToSnackBar.panelClass = ['custom-snack-success'];
          this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
          this.dialogRef.close(true);
        } else {
          dataToBeSentToSnackBar.message = res.data || 'Action failed. Please try again.';
          dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];
          this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
        }
      },
      error: (err: any) => {
        this.isLoading = false;  // Stop loader

        this.snackBarServ.openSnackBarFromComponent({
          message: err?.error?.message || 'Something went wrong. Please try again.',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        });
      },
    });
  }




getRoles(): Promise<void> {
  return new Promise((resolve, reject) => {
    const profileId = localStorage.getItem('profileId');
    const adminId = localStorage.getItem('adminId');
    const finalAdminId = adminId ? adminId : profileId;

    const currentUserRole = localStorage.getItem('profileRole'); // e.g., "Super Admin"

    this.roleservice.getresourcerole(finalAdminId).subscribe({
      next: (res: any) => {
        this.roleList = res.data
          .filter((x: any) => {
            // Always hide 'Super Admin' from dropdown
            if (x.rolename === 'Super Admin') {
              return false;
            }

            // Hide 'Admin' unless current user is Super Admin
            if (x.rolename === 'Admin' && currentUserRole !== 'Super Admin') {
              return false;
            }

            return true;
          })
          .map((x: any) => ({
            roleName: x.rolename,
            roleid: x.roleid
          }));

        resolve();
      },
      error: (err: any) => {
        console.error('Error fetching roles:', err);
        this.roleList = [];
        reject();
      }
    });
  });
}






  updateTeamMemberdetails() {
    if (this.teammember.invalid) {
      this.teammember.markAllAsTouched();

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

    const rawPhone = this.teammember.get('contactNumber')?.value;
    const parsedPhone = parsePhoneNumberFromString(rawPhone);

    let formattedPhone = rawPhone;
    if (parsedPhone) {
      formattedPhone = `+${parsedPhone.countryCallingCode} ${parsedPhone.nationalNumber}`;
    }
const email = this.teammember.get('email')?.value?.toLowerCase();
  this.teammember.get('email')?.setValue(email);  // Update form control as well if needed

    const profileId = localStorage.getItem('profileId');
    const userIdFromEdit = this.editData?.userId;

    const payload = {
      ...this.teammember.value,
      contactNumber: formattedPhone,
      updatedBy: profileId,
      userId: userIdFromEdit
    };

    this.service.updateTeamMember(payload).subscribe({
      next: (res: any) => {
        this.isLoading = false;  // Stop loader

        const dataToBeSentToSnackBar: ISnackBarData = {
          message: '',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: [],
        };

        if (res.status === 'success') {
          dataToBeSentToSnackBar.message = res.message || 'Team member updated successfully!';
          dataToBeSentToSnackBar.panelClass = ['custom-snack-success'];

          this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
          this.dialogRef.close(true);
        } else {
          dataToBeSentToSnackBar.message = res.data || 'Update failed. Please try again.';
          dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];

          this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
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
