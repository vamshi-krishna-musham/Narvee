import { Component, Inject, inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule, FormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ISnackBarData, SnackbarService } from '../../PathService/snack-bar.service';
import { TaskmanagementService } from '../../services/taskmanagement.service';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { CommonModule, DatePipe } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { MatNativeDateModule } from '@angular/material/core'; // Import MatNativeDateModule
import { MatDatepickerModule } from '@angular/material/datepicker';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatOptionModule, NativeDateAdapter } from '@angular/material/core';
import { ProjectsService } from '../../services/projects.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { trigger, transition, style, animate } from '@angular/animations';
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
  selector: 'app-add-subtaskview',
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
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,  // <-- add this
    MatProgressSpinnerModule
  ],
  providers: [
    { provide: DateAdapter, useClass: NativeDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: YOUR_DATE_FORMATS },
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },
    DatePipe
  ],
  templateUrl: './add-subtaskview.component.html',
  styleUrl: './add-subtaskview.component.scss',

})
export class AddSubtaskviewComponent {
  Taskcreate: FormGroup;
  profileId: any
  constructor(
    private dialogRef: MatDialogRef<AddSubtaskviewComponent>,
    private fb: FormBuilder,
    private service: TaskmanagementService,
    private projectservice: ProjectsService,
    private datePipe: DatePipe,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.profileId = localStorage.getItem('profileId')
    this.Taskcreate = this.fb.group({
      taskName: ['', [Validators.required, this.NameValidator , Validators.minLength(2),
        Validators.maxLength(50)]],
      taskDescription: ['', [Validators.required, this.NameValidator ,Validators.minLength(2)]],
      assignedTo: [[]], // <-- default as array
      status: [null, [Validators.required]],
      startDate: [''],
      priority: ['', [Validators.required]],
      dueDate: [''],
      duration: [{ value: '', disabled: true }],
      addedby: [this.profileId]
    });
    console.log("Received Task ID:", data.taskId); // use this.taskId for API calls

  }

// noLeadingTrailingOrOnlyWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
//   const value = control.value || '';

//   if (value.trim() === '') {
//     return { whitespace: true }; // only whitespace
//   }

//   // Check for leading/trailing spaces
//   if (value !== value.trim()) {
//     return { whitespace: true };
//   }

//   // Only allow letters and spaces between words
//   const regex = /^[A-Za-z]+( [A-Za-z]+)*$/;
//   return regex.test(value) ? null : { invalidChars: true };
// }
  dataTobeSentToSnackBarService: ISnackBarData = {
    message: '',
    duration: 2500,
    verticalPosition: 'top',
    horizontalPosition: 'center',
    direction: 'above',
    panelClass: ['custom-snack-success'],
  };
  noWhitespaceOrInvalidCharsValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';

    if (value === '') return null; // Optional: valid if empty

    if (value.trim() === '') {
      return { whitespace: true };
    }

    const regex = /^[A-Za-z]+$/; // Only letters
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
  statusList = [
    { label: 'Open', value: 'Open', color: '#4caf50' },
    { label: 'In Progress', value: 'In Progress', color: '#2196f3' },
    { label: 'In Review', value: 'In Review', color: '#ff9800' },
    { label: 'To be Tested', value: 'To be Tested', color: '#9c27b0' },
    { label: 'On Hold', value: 'On Hold', color: '#795548' },
    { label: 'Overdue', value: 'Overdue', color: '#f44336' },
    { label: 'Closed', value: 'Closed', color: '#607d8b' },
    { label: 'Blocked', value: 'Blocked', color: '#e91e63' }
  ];
  isEditMode: any
  taskId!: any
  subtaskId: any
  selectedFiles: File[] = [];
  existingFiles: any[] = [];

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

    this.projectservice.deletefile(fileToRemove.id).subscribe({
      next: (res: any) => {
        const response = res as IDeleteProjectResponse;
        const isSuccess = response.status === 'success';

        if (isSuccess) {
          // ✅ Remove from UI only after successful deletion
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
  ngOnInit() {
    console.log(this.data.projectId, 'projectId from dialog');
    console.log(this.data.pid, 'pid from dialog');
    console.log(this.data.subtaskId, 'subtaskId');
    console.log(this.data.targetdate, 'formmmmmmmmtargetdate');
    console.log(this.data.startdate, 'startdatestartdate');


    // Set edit mode only if a taskId exists
    this.isEditMode = !!this.data.subtaskId;

    // If editing, fetch task details
    if (this.isEditMode) {
      this.subtaskId = this.data.subtaskId;
      this.getTaskByIdDetails();
    }

    // Always fetch assigned users
    this.getassignedtask(this.data.projectId);
  }

  onSubmitTask() {
    if (this.isEditMode) {
      this.updateTaskdetails();
    } else {
      this.CreateTask();
    }
  }

searchAssignedUser: string = '';
assignedUsers: any[] = [];
filteredAssignedUsers: any[] = [];

getassignedtask(projectId: string) {
  this.projectservice.getAssignedDropdown(projectId).subscribe((res: any) => {
    this.assignedUsers = res.data || res;
    this.filteredAssignedUsers = [...this.assignedUsers];
    console.log(this.assignedUsers, 'assigned users');
  });
}

filterAssignedUsers() {
  const search = this.searchAssignedUser.toLowerCase();
  const selectedUsers = this.Taskcreate.get('assignedTo')?.value || [];

  this.filteredAssignedUsers = this.assignedUsers.filter(user =>
    user.fullname.toLowerCase().includes(search) || selectedUsers.includes(user)
  );
}

onAssignedDropdownOpen(opened: boolean) {
  if (!opened) {
    this.searchAssignedUser = '';
    this.filteredAssignedUsers = [...this.assignedUsers];
  }
}



  getStatusColor(value: string): string {
    return this.statusList.find(s => s.value === value)?.color || '';
  }

  getStatusLabel(value: string): string {
    return this.statusList.find(s => s.value === value)?.label || '';
  }
  priorityList = [
    { label: 'High', value: 'High', color: '#f44336' },       // Red
    { label: 'Medium', value: 'Medium', color: '#ff9800' },   // Orange
    { label: 'Low', value: 'Low', color: '#4caf50' },         // Green
    { label: 'None', value: 'None', color: '#9e9e9e' }         // Grey
  ];

  getPriorityColor(value: string): string {
    return this.priorityList.find(p => p.value === value)?.color || '';
  }

  getPriorityLabel(value: string): string {
    return this.priorityList.find(p => p.value === value)?.label || '';
  }
  dueDateFilter = (date: Date | null): boolean => {
    const start = this.Taskcreate.get('startDate')?.value;
    if (!date) return false;
    if (!start) return true;

    const startDate = new Date(start).setHours(0, 0, 0, 0);
    const currentDate = new Date(date).setHours(0, 0, 0, 0);

    return currentDate >= startDate;
  };
  startDateExceedsTarget = false;
  dueDateExceedsTarget = false;
  startDateBeforeTaskStart: boolean = false;
  dueDateBeforeTaskStart: boolean = false;
missingProjectDatesTouched = false;

  today: Date = new Date();

  onDateChange(): void {
    const start = this.Taskcreate.get('startDate')?.value;
    const end = this.Taskcreate.get('dueDate')?.value;
if ((!this.data.startdate || !this.data.targetdate) && (start || end)) {
    this.missingProjectDatesTouched = true;
    return;
  }

  // Reset if dates are provided
  this.missingProjectDatesTouched = false;
    const targetDate = new Date(this.data.targetdate);     // Task deadline
    const taskStartDate = new Date(this.data.startdate);   // Task start date

    const normalize = (d: any) => new Date(new Date(d).setHours(0, 0, 0, 0));

    const startDate = start ? normalize(start) : null;
    const dueDate = end ? normalize(end) : null;
    const projectTarget = normalize(targetDate);
    const projectStart = normalize(taskStartDate);

    //  Validation against deadline
    this.startDateExceedsTarget = !!startDate && startDate > projectTarget;
    this.dueDateExceedsTarget = !!dueDate && dueDate > projectTarget;

    // Validation against task start
    this.startDateBeforeTaskStart = !!startDate && startDate < projectStart;
    this.dueDateBeforeTaskStart = !!dueDate && dueDate < projectStart;

    // Duration logic
    if (startDate && dueDate) {
      const diffTime = dueDate.getTime() - startDate.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 3600 * 24));
      this.Taskcreate.get('duration')?.setValue(diffDays >= 0 ? diffDays : 0);
    } else {
      this.Taskcreate.get('duration')?.setValue('');
    }

    //  If due date is before start date
    if (startDate && dueDate && dueDate < startDate) {
      this.Taskcreate.get('dueDate')?.setValue('');
    }
  }

minStartDate: Date = new Date(); // Default to today
getTaskByIdDetails() {
  const subtaskId = this.data.subtaskId;
  this.projectservice.getSubTaskId(subtaskId).subscribe((res: any) => {
    const task = res.data;

    // Handle null dates safely
    const taskStartDate = task.startDate ? new Date(task.startDate) : null;
    const taskDueDate = task.targetDate ? new Date(task.targetDate) : null;

    // Set minStartDate to the earlier of today or startDate (if present)
    this.minStartDate = taskStartDate && taskStartDate < this.today ? taskStartDate : this.today;

    // Patch form fields, use null to clear date if unavailable
    this.Taskcreate.patchValue({
      taskName: task.subTaskName,
      taskDescription: task.subTaskDescription,
      status: task.status,
      startDate: taskStartDate,
      dueDate: taskDueDate,
      priority: task.priority,
      duration: task.duration
    });

    // Match assigned users
    const matchedAssigned = this.assignedUsers.filter(user =>
      task.assignedto?.some((assigned: any) => assigned.tmsUserId === user.userid)
    );

    this.Taskcreate.patchValue({
      assignedTo: matchedAssigned
    });

    this.existingFiles = res.data.files || [];
  });
}


  onCancel() {
    this.dialogRef.close();
  }
  private snackBarServ = inject(SnackbarService);
  isLoading = false;  // Add this to your component class properties

  CreateTask() {
        const startDate = this.Taskcreate.value.startDate;
  const dueDate = this.Taskcreate.value.dueDate;

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
    if (
      this.Taskcreate.invalid ||
      this.startDateExceedsTarget ||
      this.dueDateExceedsTarget
    ) {
      this.Taskcreate.markAllAsTouched();

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

    this.isLoading = true;  // Start loader here

    const formattedStartDate = this.datePipe.transform(this.Taskcreate.value.startDate, 'yyyy-MM-dd');
    const formattedTargetDate = this.datePipe.transform(this.Taskcreate.value.dueDate, 'yyyy-MM-dd');
    const selectedUsers = this.Taskcreate.value.assignedTo || [];

    const assignedToPayload = selectedUsers.map((user: { userid: any; fullname: any }) => ({
      tmsUserId: user.userid,
      fullname: user.fullname,
    }));

    const subtaskPayload = {
      subTaskName: this.Taskcreate.value.taskName,
      subTaskDescription: this.Taskcreate.value.taskDescription,
      addedby: this.profileId,
      status: this.Taskcreate.value.status,
      startDate: formattedStartDate,
      targetDate: formattedTargetDate,
      priority: this.Taskcreate.value.priority,
      assignedto: assignedToPayload,
      duration: this.Taskcreate.get('duration')?.value,
      task: {
        taskid: Number(this.data?.taskid),
      },
    };

    const formData = new FormData();
    formData.append('subtask', JSON.stringify(subtaskPayload));

    this.selectedFiles.forEach(file => {
      formData.append('files', file);
    });

    this.projectservice.CreateSubTask(formData).subscribe({
      next: (res: any) => {
        this.isLoading = false;  // Stop loader
        const isSuccess = res.status === 'success';

this.snackBarServ.openSnackBarFromComponent({
  message: res.message || (isSuccess ? 'Subtask created successfully!' : 'Creation failed.'),
  duration: 3000,
  verticalPosition: 'top',
  horizontalPosition: 'center',
  panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure'],
});

        if (isSuccess) {
          this.dialogRef.close(true);
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
      }
    });
  }


  updateTaskdetails() {
    const startDate = this.Taskcreate.value.startDate;
  const dueDate = this.Taskcreate.value.dueDate;

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
    if (
      this.Taskcreate.invalid ||
      this.startDateExceedsTarget ||
      this.dueDateExceedsTarget
    ) {
      this.Taskcreate.markAllAsTouched();

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

    const formattedStartDate = this.datePipe.transform(this.Taskcreate.value.startDate, 'yyyy-MM-dd');
    const formattedTargetDate = this.datePipe.transform(this.Taskcreate.value.dueDate, 'yyyy-MM-dd');
    const selectedUsers = this.Taskcreate.value.assignedTo || [];

    const assignedToPayload = selectedUsers.map((user: { userid: any; fullname: any }) => ({
      tmsUserId: user.userid,
      fullname: user.fullname,
    }));

    const finalPayload = {
      subTaskId: this.data.subtaskId,
      subTaskName: this.Taskcreate.value.taskName,
      subTaskDescription: this.Taskcreate.value.taskDescription,
      addedby: this.profileId,
      updatedBy: this.profileId,
      status: this.Taskcreate.value.status,
      startDate: formattedStartDate,
      targetDate: formattedTargetDate,
      priority: this.Taskcreate.value.priority,
      assignedto: assignedToPayload,
      duration: this.Taskcreate.get('duration')?.value,
      task: {
        taskid: Number(this.data?.taskid),
      },
    };

    const formData = new FormData();
    formData.append('subtask', JSON.stringify(finalPayload));

    this.selectedFiles.forEach(file => {
      formData.append('files', file);
    });

    this.projectservice.updatesubTaskdetails(formData).subscribe({
      next: (res: any) => {
        this.isLoading = false;  // Stop loader

        const isSuccess = res.status === 'success';

   this.snackBarServ.openSnackBarFromComponent({
  message: res.message || (isSuccess ? 'Subtask Updated successfully!' : 'Updation failed.'),
  duration: 3000,
  verticalPosition: 'top',
  horizontalPosition: 'center',
  panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure'],
});


        if (isSuccess) {
          this.dialogRef.close(true);
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



}
