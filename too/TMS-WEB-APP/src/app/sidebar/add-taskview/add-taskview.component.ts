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
  selector: 'app-add-taskview',

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
  templateUrl: './add-taskview.component.html',
  styleUrl: './add-taskview.component.scss'
})
export class AddTaskviewComponent {
  Taskcreate: FormGroup;
  profileId: any
  constructor(
    private dialogRef: MatDialogRef<AddTaskviewComponent>,
    private fb: FormBuilder,
    private service: TaskmanagementService,
    private projectservice: ProjectsService,
    private datePipe: DatePipe,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.profileId = localStorage.getItem('profileId')
    this.Taskcreate = this.fb.group({
      taskName: ['', [Validators.required, this.NameValidator, Validators.minLength(2),
      Validators.maxLength(50),]],
      taskDescription: ['', [Validators.required, this.NameValidator, Validators.minLength(2)]],
      assignedTo: [[]], // <-- default as array
      status: [null, [Validators.required]],
      startDate: [''],
      priority: ['', [Validators.required]],
      dueDate: [''],
      duration: [{ value: '', disabled: true }],
      addedby: [this.profileId]
    });
    console.log("Received Task ID:", data.taskId); // use this.taskId for API calls
    console.log(data.targetDate, 'targetDate');
    console.log(data.startDate, 'this.startDate');


  }

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

  dataTobeSentToSnackBarService: ISnackBarData = {
    message: '',
    duration: 2500,
    verticalPosition: 'top',
    horizontalPosition: 'center',
    direction: 'above',
    panelClass: ['custom-snack-success'],
  };
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
  isEditMode: any
  taskId!: any
  ngOnInit() {
    console.log(this.data.projectId, 'projectId from dialog');
    console.log(this.data.pid, 'pid from dialog');
    console.log(this.data.targetDate, 'targetDatengoninit');
    console.log(this.data.startDate, 'startDatengoninit');
    console.log(this.data.projectStatus, 'projectstatusss');


    // Set edit mode only if a taskId exists
    this.isEditMode = !!this.data.taskId;

    // If editing, fetch task details
    if (this.isEditMode) {
      this.taskId = this.data.taskId;
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
    this.filteredAssignedUsers = this.assignedUsers;
    console.log(this.assignedUsers, 'assigned users');
  });
}

filterAssignedUsers() {
  const search = this.searchAssignedUser.toLowerCase();
  const selectedUsers = this.Taskcreate.get('assignedTo')?.value || [];

    this.filteredAssignedUsers = this.assignedUsers.filter(member =>
    member.fullname.toLowerCase().includes(search) || selectedUsers.includes(member)
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
  startDateBeforeProject: boolean = false; // ✅ Add this
  dueDateBeforeProject: boolean = false;   // ✅ And this
  missingProjectDatesTouched = false;

  today: Date = new Date();

  onDateChange(): void {
    const start = this.Taskcreate.get('startDate')?.value;
    const end = this.Taskcreate.get('dueDate')?.value;
    // If user attempts to pick a date but project start/target date is missing
    if ((!this.data.startDate || !this.data.targetDate) && (start || end)) {
      this.missingProjectDatesTouched = true;
      return;
    }

    // Reset if dates are provided
    this.missingProjectDatesTouched = false;
    const targetDate = new Date(this.data.targetDate); // Project end
    const projectStartDate = new Date(this.data.startDate); // Project start

    const normalize = (d: any) => new Date(new Date(d).setHours(0, 0, 0, 0));

    const startDate = start ? normalize(start) : null;
    const dueDate = end ? normalize(end) : null;
    const projectTarget = normalize(targetDate);
    const projectStart = normalize(projectStartDate);

    // Error if start or due date is after project end
    this.startDateExceedsTarget = !!startDate && startDate > projectTarget;
    this.dueDateExceedsTarget = !!dueDate && dueDate > projectTarget;

    // Error if start or due date is before project start
    this.startDateBeforeProject = !!startDate && startDate < projectStart;
    this.dueDateBeforeProject = !!dueDate && dueDate < projectStart;

    console.log(this.startDateBeforeProject, 'startDateBeforeProject');

    // 🔁 Duration logic
    if (startDate && dueDate) {
      const diffTime = dueDate.getTime() - startDate.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 3600 * 24));
      this.Taskcreate.get('duration')?.setValue(diffDays >= 0 ? diffDays : 0);
    } else {
      this.Taskcreate.get('duration')?.setValue('');
    }

    if (startDate && dueDate && dueDate < startDate) {
      this.Taskcreate.get('dueDate')?.setValue('');
    }
  }



  existingFiles: any[] = [];
  minStartDate: Date = new Date(); // default to today

 getTaskByIdDetails() {
  const taskId = this.data.taskId;
  this.projectservice.getTaskByIdDetails(taskId).subscribe((res: any) => {
    const task = res.data;

    // Handle null dates
    const taskStartDate = task.startDate ? new Date(task.startDate) : null;
    const taskDueDate = task.targetDate ? new Date(task.targetDate) : null;

    // Set minStartDate safely
    this.minStartDate = taskStartDate && taskStartDate < this.today ? taskStartDate : this.today;

    // Patch fields with null-safe date values
    this.Taskcreate.patchValue({
      taskName: task.taskname,
      taskDescription: task.description,
      status: task.status,
      startDate: taskStartDate,
      dueDate: taskDueDate,
      priority: task.priority,
      duration: task.duration
    });

    // Handle assigned users
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

  isLoading = false;
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

    this.isLoading = true; // Show loader

    const formattedStartDate = this.datePipe.transform(this.Taskcreate.value.startDate, 'yyyy-MM-dd');
    const formattedTargetDate = this.datePipe.transform(this.Taskcreate.value.dueDate, 'yyyy-MM-dd');
    const selectedUsers = this.Taskcreate.value.assignedTo || [];

    const assignedToPayload = selectedUsers.map((user: { userid: any; fullname: any }) => ({
      tmsUserId: user.userid,
      fullname: user.fullname,
    }));

    const taskPayload = {
      taskname: this.Taskcreate.value.taskName,
      description: this.Taskcreate.value.taskDescription,
      addedby: this.profileId,
      status: this.Taskcreate.value.status,
      startDate: formattedStartDate,
      targetDate: formattedTargetDate,
      priority: this.Taskcreate.value.priority,
      assignedto: assignedToPayload,
      duration: this.Taskcreate.get('duration')?.value,
      project: {
        pid: Number(this.data?.pid),
      },
    };

    const formData = new FormData();
    formData.append('task', JSON.stringify(taskPayload));
    this.selectedFiles.forEach(file => {
      formData.append('files', file);
    });

    this.projectservice.CreateTask(formData).subscribe({
      next: (res: any) => {
        this.isLoading = false; // Hide loader
        const isSuccess = res.status === 'success';

        const dataToBeSentToSnackBar: ISnackBarData = {
          message: res.message || (isSuccess ? 'Task created successfully!' : 'Creation failed.'),
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
        this.isLoading = false; // Hide loader on error
        const dataToBeSentToSnackBar: ISnackBarData = {
          message: err?.error?.message || 'Something went wrong. Please try again.',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        };
        this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      },
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

    this.isLoading = true;

    const formattedStartDate = this.datePipe.transform(this.Taskcreate.value.startDate, 'yyyy-MM-dd');
    const formattedTargetDate = this.datePipe.transform(this.Taskcreate.value.dueDate, 'yyyy-MM-dd');
    const selectedUsers = this.Taskcreate.value.assignedTo || [];

    const assignedToPayload = selectedUsers.map((user: { userid: any; fullname: any }) => ({
      tmsUserId: user.userid,
      fullname: user.fullname,
    }));

    const finalPayload = {
      taskname: this.Taskcreate.value.taskName,
      description: this.Taskcreate.value.taskDescription,
      addedby: this.profileId,
      updatedby: this.profileId,
      status: this.Taskcreate.value.status,
      startDate: formattedStartDate,
      targetDate: formattedTargetDate,
      priority: this.Taskcreate.value.priority,
      taskid: this.data.taskId,
      assignedto: assignedToPayload,
      duration: this.Taskcreate.get('duration')?.value,
      project: {
        pid: Number(this.data?.pid)
      }
    };

    const formData = new FormData();
    formData.append('task', JSON.stringify(finalPayload));
    this.selectedFiles.forEach(file => {
      formData.append('files', file);
    });

    this.projectservice.updateTaskdetails(formData).subscribe({
      next: (res: any) => {
        this.isLoading = false;
        const isSuccess = res.status === 'success';

        this.snackBarServ.openSnackBarFromComponent({
          message: res.message || (isSuccess ? 'Task Updated successfully!' : 'Task Updation failed.'),
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
        this.isLoading = false;
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
}