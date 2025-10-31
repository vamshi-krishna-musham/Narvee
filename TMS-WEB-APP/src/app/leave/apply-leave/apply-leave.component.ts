import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveService, LeaveRequest } from '../../services/leave.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

const LEAVE_TYPES = ['Sick','Casual','Emergency','Long Leave'] as const;
type LeaveType = typeof LEAVE_TYPES[number];

type ApplyLeaveDialogData = {
  prefill?: {
    leaveType?: LeaveType;   // ← use the exported alias
    startDate?: Date | string;
    endDate?: Date | string;
    reason?: string;
  };
  balance?: number;
};

@Component({
  selector: 'app-apply-leave',
  templateUrl: './apply-leave.component.html',
  styleUrls: ['./apply-leave.component.scss'],
})
export class ApplyLeaveComponent implements OnInit {
  minDate = this.toDateOnly(new Date());
  durationDays = 0;
  leaveBalance = 0;
  private holidaysYMD: string[] = [];
  private holidaySet = new Set(this.holidaysYMD);

  form = this.fb.group({
    leaveType: this.fb.control<LeaveType | null>(null, { validators: [Validators.required] }),
    startDate: this.fb.control<Date | null>(null, { validators: [Validators.required] }),
    endDate:   this.fb.control<Date | null>(null, { validators: [Validators.required] }),
    reason:    this.fb.control<string>('', { validators: [Validators.required, Validators.maxLength(100)] }),
  });
  existingleaves: LeaveRequest[] = [];

  constructor(
    private fb: FormBuilder,
    private leave: LeaveService,
    private snack: MatSnackBar,
    private dialogRef: MatDialogRef<ApplyLeaveComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ApplyLeaveDialogData
  ) {}

private normalizeDateInput(v: Date | string | undefined | null): Date | null {
    if (!v) return null;
    return typeof v === 'string' ? new Date(v) : v;
  }

  ngOnInit(): void {
    // Prefill (optional)
    this.leaveBalance = this.data?.balance ?? 0;
    if (this.data?.prefill) {
      const { leaveType, startDate, endDate, reason } = this.data.prefill;
      this.form.patchValue({
        leaveType,
        startDate: this.normalizeDateInput(startDate),  // ← convert to Date
        endDate:   this.normalizeDateInput(endDate),    // ← convert to Date
        reason
      });
    }

    // Enforce 2-week notice for Casual, else allow today
    this.form.get('leaveType')?.valueChanges.subscribe((type) => {
      const today = this.toDateOnly(new Date());
      if (type === 'Casual') {
        this.minDate = new Date(today.setDate(today.getDate() + 14));
      } else {
        this.minDate = new Date();
      }
    });

    this.form.valueChanges.subscribe(() => this.computeDuration());
    setTimeout(() => this.computeDuration());
    this.loadLeaves();
  }

  loadLeaves(): void {
    const profileId = Number(localStorage.getItem('profileId'));
    this.leave.listMine(profileId).subscribe({
      next: (data) => (this.existingleaves = data),
      error: (err) =>
        this.snack.open(err.error?.message || 'Failed to load leaves', 'OK', {
          duration: 3000,
          panelClass: ['custom-snack-failure'],
          horizontalPosition: 'center',
          verticalPosition: 'top',
        }),
    });
  }

  private computeDuration(): void {
    const s = this.form.get('startDate')!.value as Date | string | null;
    const e = this.form.get('endDate')!.value as Date | string | null;

    if (!s || !e) {
      this.durationDays = 0;
      return;
    }

    const start = this.toDateOnly(s);
    const end = this.toDateOnly(e);
    if (end.getTime() < start.getTime()) {
      this.durationDays = 0;
      return;
    }

    this.durationDays = this.businessDaysBetween(start, end);
  }

  private businessDaysBetween(start: Date, end: Date): number {
    let count = 0;
    const cur = new Date(start);
    while (cur.getTime() <= end.getTime()) {
      const d = new Date(cur.getFullYear(), cur.getMonth(), cur.getDate());
      if (!this.isWeekend(d) && !this.isHoliday(d)) count++;
      cur.setDate(cur.getDate() + 1);
    }
    return count;
  }

  private isWeekend(d: Date): boolean {
    const w = d.getDay();
    return w === 0 || w === 6;
  }

  private isHoliday(d: Date): boolean {
    return this.holidaySet.has(this.toYMD(d));
  }

  private toDateOnly(v: string | Date): Date {
    const d = typeof v === 'string' ? new Date(v) : v;
    return new Date(d.getFullYear(), d.getMonth(), d.getDate());
  }

  private toYMD(d: Date): string {
    const y = d.getFullYear();
    const m = `${d.getMonth() + 1}`.padStart(2, '0');
    const day = `${d.getDate()}`.padStart(2, '0');
    return `${y}-${m}-${day}`;
  }
  getMaxEndDate(): Date | null {
  const start = this.form.get('startDate')?.value;
  const type = this.form.get('leaveType')?.value;
  if (type === 'Long Leave') return null;
  if (start) {
    const max = new Date(start);
    max.setDate(max.getDate() + 7);
    return max;
      }
      return null;
    }


  submit(): void {
    if (this.form.invalid || this.durationDays <= 0) {
      this.snack.open(
        this.durationDays <= 0
          ? 'No working days in selected range'
          : 'Please fill all required fields',
        'OK',
        {
          duration: 2800,
          panelClass: ['custom-snack-failure'],
          horizontalPosition: 'center',
          verticalPosition: 'top',
        }
      );
      return;
    }

    const start = this.toDateOnly(this.form.value.startDate!);
    const end = this.toDateOnly(this.form.value.endDate!);

    let hasOverlap = false;
  this.existingleaves.forEach((leave) => {
  const existingFrom = this.toDateOnly(leave.startDate);
  const existingTo   = new Date(this.toDateOnly(leave.endDate).getTime() + 86400000);
  const existingStatus = (leave.status ?? '');  // ← normalize to string

  const isBlocking = ['APPROVED','PENDING','REJECTED'].includes(existingStatus); // ← now a string

  if (
    (start >= existingFrom && start <= existingTo && isBlocking) ||
    (end   >= existingFrom && end   <= existingTo && isBlocking) ||
    (start <= existingFrom && end   >= existingTo && isBlocking)
  ) {
    hasOverlap = true;
  }
});


    if (hasOverlap) {
      this.snack.open(
        'Leave dates overlap with existing pending/approved leave',
        'OK',
        {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['custom-snack-failure'],
        }
      );
      return;
    }
      if (this.leaveBalance <= 0) {
    this.snack.open(
      'You have no remaining leave balance. Please contact your manager for assistance.',
      'OK',
      {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['custom-snack-failure'],
      }
    );
    return;
  }
      if (this.durationDays > this.leaveBalance) {
    this.snack.open(
      `You only have ${this.leaveBalance} leave days remaining. Please reduce your duration.`,
      'OK',
      {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['custom-snack-failure'],
      }
    );
    return;
  }

    const payload = {
      userId: Number(localStorage.getItem('profileId')),
      userName:
        (localStorage.getItem('firstName') || '') +
          ' ' +
          (localStorage.getItem('lastName') || '') || 'Unknown',
      leaveCategory: this.form.value.leaveType,
      fromDate: this.toYMD(start),
      toDate: this.toYMD(end),
      reason: (this.form.value.reason || '').trim(),
      status: 'PENDING',
      duration: this.durationDays,
    };

    this.leave.apply(payload as any).subscribe({
      next: () => {
        this.snack.open('Leave submitted successfully', 'OK', {
          duration: 2500,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
          panelClass: ['custom-snack-success'],
        });
        // Close dialog and tell parent to refresh
        this.dialogRef.close(true);
      },
      error: (e) => {
        this.snack.open(e.error?.message || 'Submission failed', 'OK', {
          duration: 3500,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
          panelClass: ['custom-snack-failure'],
        });
      },
    });
  }

  cancel(): void {
    // Close like other dialogs rather than routing away
    this.dialogRef.close(false);
  }
}
