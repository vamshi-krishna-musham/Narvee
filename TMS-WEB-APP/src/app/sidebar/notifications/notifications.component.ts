import { CommonModule } from '@angular/common';
import { Component, inject, TemplateRef, ViewChild } from '@angular/core';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatOptionModule } from '@angular/material/core';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSlideToggleChange, MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TaskmanagementService } from '../../services/taskmanagement.service';
import { SnackbarService, ISnackBarData } from '../../PathService/snack-bar.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [
    MatTableModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSortModule,
    MatPaginatorModule,
    CommonModule,
    MatTooltipModule,
    ReactiveFormsModule,
    FormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    MatDialogModule,
    MatListModule,
    MatSlideToggleModule
  ],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.scss'
})
export class NotificationsComponent {
[x: string]: any;
  dataTableColumns: string[] = ['SerialNum', 'NotificationType', 'NotificationStatus'];
  dataSource = new MatTableDataSource<any>();
  @ViewChild('emailEditDialog') emailEditDialog!: TemplateRef<any>;
  @ViewChild('editFieldsDialog') editFieldsDialog!: TemplateRef<any>;

  dialogRef!: MatDialogRef<any>;
  editDialogRef!: MatDialogRef<any>;
  emailForm!: FormGroup;
  private snackBarServ = inject(SnackbarService);

  constructor(private dialog: MatDialog, private fb: FormBuilder,private taskservice:TaskmanagementService) { }


  notificationTypes = [
    { code: 'project_create', name: 'Project Create' },
    { code: 'project_update', name: 'Project Update' },
    { code: 'task_create', name: 'Task Create' },
    { code: 'task_update', name: 'Task Update' },
    { code: 'task_status', name: 'Task Status' },
    { code: 'task_comment', name: 'Task Comment' },
    { code: 'subtask_create', name: 'Subtask Create' },
    { code: 'subtask_update', name: 'Subtask Update' },
    { code: 'subtask_status', name: 'Subtask Status' },
    { code: 'subtask_comment', name: 'Subtask Comment' },
  ];

ngOnInit() {
  const data = this.notificationTypes.map((type, index) => ({
    serialNum: index + 1,
    taskName: type.code,
    enabled: false
  }));

 this.emailForm = this.fb.group({
  subject: ['', this.optionalTrimmedValidator()],
  cc: ['', [this.optionalTrimmedValidator(), this.emailListValidator()]],
  bcc: ['', [this.optionalTrimmedValidator(), this.emailListValidator()]],
}, 
);


  this.dataSource.data = data;
  this.GetNotification();
}


  optionalTrimmedValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (value && typeof value === 'string' && !value.trim()) {
      return { whitespaceOnly: true };
    }
    return null;
  };
}

emailListValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value || typeof value !== 'string') return null;

    const emails = value.split(',').map(e => e.trim()).filter(Boolean);
    const invalid = emails.filter(e => !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(e));

    return invalid.length ? { invalidEmailList: true } : null;
  };
}



  getNotificationLabel(code: string): string {
    const match = this.notificationTypes.find(t => t.code === code);
    return match ? match.name : code;
  }


submitEmailForm() {
  this.emailForm.markAllAsTouched();

  const rawCc = this.emailForm.value.cc?.trim();
  const rawBcc = this.emailForm.value.bcc?.trim();
  const rawSubject = this.emailForm.value.subject?.trim();

  const ccFilled = !!rawCc;
  const bccFilled = !!rawBcc;
  const subjectFilled = !!rawSubject;

  if (!ccFilled && !bccFilled && !subjectFilled) {
    this.snackBarServ.openSnackBarFromComponent({
      message: 'Please fill at least one field: Subject, CC, or BCC.',
      duration: 3000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-failure'],
    });
    return;
  }



  const profileId = localStorage.getItem('profileId');
  const adminId = localStorage.getItem('adminId');
  const finalAdminId = adminId ? adminId : profileId;

  const cc = rawCc ? rawCc.split(',').map((e: string) => e.trim()).filter(Boolean) : null;
  const bcc = rawBcc ? rawBcc.split(',').map((e: string) => e.trim()).filter(Boolean) : null;
  const subject = subjectFilled ? rawSubject : null;

  const payload = {
    id: this.currentElement?.id || null,
    adminId: Number(finalAdminId),
    emailNotificationType: this.currentElement.taskName.toUpperCase(),
    isEnabled: true,
    ccMails: cc,
    bccMails: bcc,
    subject: subject
  };

  const request$ = this.currentElement?.id
    ? this.taskservice.NotificationUpdate(payload)
    : this.taskservice.NotificationSave([payload]);

  request$.subscribe({
    next: (res: any) => {
      this.snackBarServ.openSnackBarFromComponent({
        message: res.message || 'Notification saved!',
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-success'],
      });
      this.editDialogRef.close();
      this.emailForm.reset();
      this.GetNotification();
    },
    error: () => {
      this.snackBarServ.openSnackBarFromComponent({
        message: 'Something went wrong. Please try again.',
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure'],
      });
    }
  });
}



GetNotification() {
  const profileId = localStorage.getItem('profileId');
  const adminId = localStorage.getItem('adminId');
  const finalAdminId = adminId ? adminId : profileId;

  this.taskservice.GetNotification(finalAdminId).subscribe((res: any) => {
    if (res?.data?.length) {
      const updatedData = this.dataSource.data.map((item) => {
        const matched = res.data.find(
          (entry: any) => entry.emailNotificationType === item.taskName.toUpperCase()
        );

        return {
          ...item,
          id: matched?.id || null,
          enabled: matched ? matched.isEnabled : false
        };
      });

      this.dataSource.data = updatedData;
    }
  });
}


currentElement: any = null;



toggleStatus(event: MatSlideToggleChange, element: any) {
  const intendedValue = event.checked;
  this.currentElement = element;

  // Revert toggle until the user confirms
  event.source.checked = !intendedValue;

  if (intendedValue) {
    // User is turning ON → ask for confirmation
    this.dialogRef = this.dialog.open(this.emailEditDialog);

    this.dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        // YES: Open the form for editing
        element.enabled = true;
        event.source.checked = true;
        this.openEditFields(); // opens the dialog to edit subject/cc/bcc
      } else {
        // NO: Directly call save/update API with null values (no validation)
        element.enabled = true;
        event.source.checked = true;

        const profileId = localStorage.getItem('profileId');
        const adminId = localStorage.getItem('adminId');
        const finalAdminId = adminId ? adminId : profileId;

        const payload = {
          id: element?.id || null,
          adminId: Number(finalAdminId),
          emailNotificationType: element.taskName.toUpperCase(),
          isEnabled: true,
          ccMails: null,
          bccMails: null,
          subject: null
        };

        const request$ = element?.id
          ? this.taskservice.NotificationUpdate(payload)
          : this.taskservice.NotificationSave([payload]);

        request$.subscribe({
          next: (res: any) => {
            this.snackBarServ.openSnackBarFromComponent({
              message: res.message || 'Notification saved!',
              duration: 3000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['custom-snack-success'],
            });
            this.GetNotification();
          },
          error: () => {
            this.snackBarServ.openSnackBarFromComponent({
              message: 'Something went wrong. Please try again.',
              duration: 3000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['custom-snack-failure'],
            });
          }
        });
      }
    });
  } else {
    // User is turning OFF → disable directly
    element.enabled = false;

    const profileId = localStorage.getItem('profileId');
    const adminId = localStorage.getItem('adminId');
    const finalAdminId = adminId ? adminId : profileId;

    const payload = {
      id: element?.id || null,
      adminId: Number(finalAdminId),
      emailNotificationType: element.taskName.toUpperCase(),
      isEnabled: false,
      ccMails: null,
      bccMails: null,
      subject: null
    };

    const request$ = element?.id
      ? this.taskservice.NotificationUpdate(payload)
      : this.taskservice.NotificationSave([payload]);

    request$.subscribe({
      next: (res: any) => {
        this.snackBarServ.openSnackBarFromComponent({
          message: res.message || 'Notification disabled.',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-success'],
        });
        this.GetNotification();
      },
      error: () => {
        this.snackBarServ.openSnackBarFromComponent({
          message: 'Failed to update notification.',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        });
      }
    });
  }
}


openEditFields() {
  this.dialogRef.close(); // close confirmation dialog
  this.editDialogRef = this.dialog.open(this.editFieldsDialog);
}
closeEmailFormDialog(): void {
  this.emailForm.reset();          //  Reset form fields
  this.editDialogRef.close();      // Close the dialog
}
closeEditDialog() {
  this.editDialogRef.close();
  this.emailForm.reset();

  // Revert toggle if user cancelled after clicking "Yes"
  if (this.currentElement) {
    this.currentElement.enabled = false;

    // Wait for view update to reflect toggle reversal
    setTimeout(() => {
      const toggle = document.querySelector(
        `mat-slide-toggle[data-id="${this.currentElement.taskName}"] input`
      ) as HTMLInputElement;
      if (toggle) {
        toggle.checked = false;
      }
    });
  }
}

updateBasicNotification(element: any) {
  if (this.emailForm) {
    this.emailForm.markAllAsTouched();

    const rawSubject = this.emailForm.value.subject?.trim();
    const rawCc = this.emailForm.value.cc?.trim();
    const rawBcc = this.emailForm.value.bcc?.trim();

    const hasSubject = !!rawSubject;
    const hasCc = !!rawCc;
    const hasBcc = !!rawBcc;

    if (!hasSubject && !hasCc && !hasBcc) {
      this.snackBarServ.openSnackBarFromComponent({
        message: 'Please fill at least one field: Subject, CC, or BCC.',
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure'],
      });
      return;
    }

 
  }

  const profileId = localStorage.getItem('profileId');
  const adminId = localStorage.getItem('adminId');
  const finalAdminId = adminId ? adminId : profileId;

  const payload = {
    id: element?.id || null,
    adminId: Number(finalAdminId),
    emailNotificationType: element.taskName.toUpperCase(),
    isEnabled: true,
    ccMails: null,
    bccMails: null,
    subject: null
  };

  const request$ = element?.id
    ? this.taskservice.NotificationUpdate(payload)
    : this.taskservice.NotificationSave([payload]);

  request$.subscribe({
    next: (res: any) => {
      this.snackBarServ.openSnackBarFromComponent({
        message: res.message || 'Notification updated.',
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-success'],
      });
      this.GetNotification();
    },
    error: () => {
      this.snackBarServ.openSnackBarFromComponent({
        message: 'Failed to update notification.',
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure'],
      });
    }
  });
}





}
