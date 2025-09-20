import { Component, Inject, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators, FormControl, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatNativeDateModule, MatRippleModule, NativeDateAdapter } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { IDropdownSettings, NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { Project } from '../sidenav/project-model';
import { ProjectsService } from '../../services/projects.service';
import { ISnackBarData, SnackbarService } from '../../PathService/snack-bar.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject, Observable, startWith, map, takeUntil } from 'rxjs';
import { TaskService } from '../../PathService/task.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

export const YOUR_DATE_FORMATS = {
  parse: {
    dateInput: 'YYYY-MM-DD',
  },
  display: {
    dateInput: 'YYYY-MM-DD',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'YYYY-MM-DD',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};
interface IDeleteProjectResponse {
  status: string;
  message?: string;
}

@Component({
  selector: 'app-add-project',
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
  providers: [
    { provide: DateAdapter, useClass: NativeDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: YOUR_DATE_FORMATS },
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },
    DatePipe
  ],
  templateUrl: './add-project.component.html',
  styleUrl: './add-project.component.scss'
})
export class AddProjectComponent {

  private projectServ = inject(ProjectsService);
  private snackBarServ = inject(SnackbarService);
  projectForm: any = FormGroup;
  constructor(private fb: FormBuilder, private datePipe: DatePipe, private dialogRef: MatDialogRef<AddProjectComponent>, @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    console.log('Dialog Data:', this.data);
    console.log(this.data.ProjectData,'editdataaa');
    
    const pid = this.data.ProjectData?.pid;

    console.log(pid, 'piddddattaaa');
    const profileId = localStorage.getItem('profileId');
    const adminId = localStorage.getItem('adminId');
    const finalAdminId = adminId ? adminId : profileId;
    this.projectForm = this.fb.group({
      projectName: ['', [Validators.required, this.NameValidator, Validators.minLength(2),
      Validators.maxLength(50)]],
      description: ['', [Validators.required, this.NameValidator, Validators.minLength(2),]],
      addedBy: [profileId],
      status: ['To Do'],
      assignedTo: [[], Validators.required],
      startDate: [''],
      dueDate: [''],
      adminId: finalAdminId
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
  existingFiles: any[] = [];
  dueDateFilter = (date: Date | null): boolean => {
    const startDate = this.projectForm.get('startDate')?.value;
    if (!date) return false;

    if (!startDate) return true; // If start date not selected, allow all dates

    // Convert both dates to time for comparison
    return date.getTime() >= new Date(startDate).setHours(0, 0, 0, 0);
  };
  today: Date = new Date();


  ngOnInit() {
    console.log(this.data.ro);
    
    const pid = this.data?.ProjectData?.pid;
    this.teammember()

    if (pid) {
      this.projectForm.get('startDate')?.valueChanges.subscribe(() => {
        this.projectForm.get('dueDate')?.updateValueAndValidity();
      });
      const adminId = localStorage.getItem('adminId')
      const profileId = localStorage.getItem('profileId');
      const userId = adminId || profileId
      this.projectServ.getteammembersdropdown(userId).subscribe((res: any) => {
        this.teammembers = res.data;
        this.filteredMembers=res.data
        this.getProjectDetailsById(pid);
      });
    }
  }
  minStartDate: Date = new Date();

 getProjectDetailsById(pid: any) {
  this.projectServ.getProjectDetailsbyId(pid).subscribe((res: any) => {
    console.log(res, 'project details response');

    const start = res.data.startDate ? new Date(res.data.startDate) : null;
    const due = res.data.targetDate ? new Date(res.data.targetDate) : null;

    this.minStartDate = start && start < this.today ? start : this.today;

    this.projectForm.patchValue({
      projectName: res.data.projectName,
      description: res.data.description,
      status: res.data.status,
      startDate: start, // null will clear the field
      dueDate: due      // null will clear the field
    });

    const assignedUserIds = res.data.assignedto?.map((item: any) => item.tmsUserId) || [];
    const selectedUsers = this.teammembers.filter((member: any) =>
      assignedUserIds.includes(member.userId)
    );
    this.projectForm.patchValue({ assignedTo: selectedUsers });

    this.existingFiles = res.data.files || [];
  });
}



searchUser: string = '';
teammembers: any[] = [];
filteredMembers: any[] = [];

teammember() {
  const adminId = localStorage.getItem('adminId');
  const profileId = localStorage.getItem('profileId');
  const userId = adminId || profileId;

  this.projectServ.getteammembersdropdown(userId).subscribe((res: any) => {
    this.teammembers = res.data;
    this.filteredMembers = res.data;
  });
  
}

filterUsers() {
  const search = this.searchUser.toLowerCase();
  const selectedUsers = this.projectForm.get('assignedTo')?.value || [];

  this.filteredMembers = this.teammembers.filter(member =>
    member.userName.toLowerCase().includes(search) || selectedUsers.includes(member)
  );
}


onDropdownOpen(opened: boolean) {
  if (!opened) {
    this.searchUser = '';
    this.filteredMembers = [...this.teammembers];
  }
}

  isSubmitting = false;
  selectedFiles: File[] = [];

  onFileChange(event: any): void {
    const files: FileList = event.target.files;
    for (let i = 0; i < files.length; i++) {
      this.selectedFiles.push(files[i]);
    }
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }
  removeExistingFile(index: number) {
    const fileToRemove = this.existingFiles[index];

    this.projectServ.deletefile(fileToRemove.id).subscribe({
      next: (res: any) => {
        const response = res as IDeleteProjectResponse;
        const isSuccess = response.status === 'success';

        if (isSuccess) {
          this.existingFiles.splice(index, 1);
        }

        const dataToBeSentToSnackBar: ISnackBarData = {
          message: response.message || (isSuccess ? 'File deleted successfully!' : 'Failed to delete project.'),
          duration: 2500,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure'],
        };

        this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      },

      error: (err) => {
        console.error('Failed to delete file:', err);

        const dataToBeSentToSnackBar: ISnackBarData = {
          message: err?.error?.message || 'Failed to delete file. Please try again.',
          duration: 2500,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        };

        this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      }
    });
  }





  createProject() {
    const startDate = this.projectForm.value.startDate;
    const dueDate = this.projectForm.value.dueDate;

    if (!startDate && dueDate) {
      const dataToBeSentToSnackBar: ISnackBarData = {
        message: 'Please select the Start Date before selecting the Due Date.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        direction: 'above',
        panelClass: ['custom-snack-failure'],
      };

      this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      return;
    }
    if (this.projectForm.invalid) {
      this.projectForm.markAllAsTouched();

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

    this.isSubmitting = true;
    const formattedStartDate = this.datePipe.transform(this.projectForm.value.startDate, 'yyyy-MM-dd');
    const formattedTargetDate = this.datePipe.transform(this.projectForm.value.dueDate, 'yyyy-MM-dd');
    const rawFormValue = this.projectForm.value;

    const projectPayload = {
      projectName: rawFormValue.projectName,
      description: rawFormValue.description,
      addedBy: rawFormValue.addedBy,
      status: rawFormValue.status,
      adminId: rawFormValue.adminId,

      startDate: formattedStartDate,
      targetDate: formattedTargetDate,
      assignedto: rawFormValue.assignedTo.map((user: any) => ({
        tmsUserId: user.userId,
        fullname: user.userName,

      }))
    };

    const formData = new FormData();

    // ✅ Append project as a single JSON object
    formData.append('project', JSON.stringify(projectPayload));

    // ✅ Append each selected file under "files"
    this.selectedFiles.forEach((file: File) => {
      formData.append('files', file);
    });

    this.projectServ.teamMemberSave(formData).subscribe({
      next: (res: any) => {
        this.isSubmitting = false;
        const isSuccess = res.status === 'success';

        const dataToBeSentToSnackBar: ISnackBarData = {
          message: res.message || (isSuccess ? 'Project created successfully!' : 'Failed to create project.'),
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
        this.isSubmitting = false;
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



  updateProject() {
    const startDate = this.projectForm.value.startDate;
    const dueDate = this.projectForm.value.dueDate;

    // Check: Due date selected without start date
    if (!startDate && dueDate) {
      const dataToBeSentToSnackBar: ISnackBarData = {
        message: 'Please select the Start Date before selecting the Due Date.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        direction: 'above',
        panelClass: ['custom-snack-failure'],
      };

      this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      return;
    }
    if (this.projectForm.invalid) {
      this.projectForm.markAllAsTouched();

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

    this.isSubmitting = true;
    const formattedStartDate = this.datePipe.transform(this.projectForm.value.startDate, 'yyyy-MM-dd');
    const formattedTargetDate = this.datePipe.transform(this.projectForm.value.dueDate, 'yyyy-MM-dd');
    const rawFormValue = this.projectForm.value;
    const profileId = localStorage.getItem('profileId');
    const pid = this.data?.ProjectData?.pid;

    const payload = {
      pid: pid,
      projectName: rawFormValue.projectName,
      description: rawFormValue.description,
      addedBy: rawFormValue.addedBy,
      updatedBy: profileId,
      status: rawFormValue.status,
      startDate: formattedStartDate,
      targetDate: formattedTargetDate,
      adminId: rawFormValue.adminId,

      assignedto: rawFormValue.assignedTo.map((user: any) => ({
        tmsUserId: user.userId,
        fullname: user.userName,

      }))
    };
    console.log(payload, 'payloaddd');

    const formData = new FormData();
    formData.append('project', JSON.stringify(payload));



    // Append new files (actual File objects)
    this.selectedFiles.forEach(file => {
      formData.append('files', file); // same 'files' key
    });

    this.projectServ.updateProjectDetails(formData).subscribe({
      next: (res: any) => {
        this.isSubmitting = false;
        const isSuccess = res.status === 'success';

        const dataToBeSentToSnackBar: ISnackBarData = {
          message: res.message || (isSuccess ? 'Project updated successfully!' : 'Failed to update project.'),
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
        this.isSubmitting = false;
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





  onCancel() {
    this.dialogRef.close();
  }


}
