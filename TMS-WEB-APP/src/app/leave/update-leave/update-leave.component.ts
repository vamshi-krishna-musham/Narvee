import { Component, OnInit, TemplateRef, ViewChild, Inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveService, LeaveRequest } from '../../services/leave.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export const LEAVE_TYPES = ['Sick','Casual','Emergency'] as const;
export type LeaveType = typeof LEAVE_TYPES[number];

type UpdateLeaveData = { id: number,balance?: number };

@Component({
  selector: 'app-update-leave',
  templateUrl: './update-leave.component.html',
  styleUrls: ['./update-leave.component.scss']
})
export class UpdateLeaveComponent implements OnInit {
  @ViewChild('cancelDialog') cancelDialogTpl!: TemplateRef<any>;
  leaveBalance = 0;
  form = this.fb.group({
    leaveType: this.fb.control<LeaveType | null>(null, { validators: [Validators.required] }),
    startDate: this.fb.control<Date | null>(null, { validators: [Validators.required] }),
    endDate:   this.fb.control<Date | null>(null, { validators: [Validators.required] }),
    duration:  this.fb.control<number | null>(null),
    reason:    this.fb.control<string>('', { validators: [Validators.required, Validators.maxLength(100)] }),
    status: this.fb.control<string>('PENDING', { validators: [Validators.required] })
  });
  existingleaves: LeaveRequest[] = [];
  durationDays = 0;
  leaveId!: number;

  private holidaysYMD: string[] = [];
  private holidaySet = new Set(this.holidaysYMD);
  minDate = this.toDateOnly(new Date());

  // store originals (normalized) to detect eligible changes
  originalLeaveType: string | null = null;
  originalStartDateYMD: string | null = null;
  originalEndDateYMD:   string | null = null;

  constructor(
    private fb: FormBuilder,
    private leave: LeaveService,
    private snack: MatSnackBar,
    private dialog: MatDialog,
    private dialogRef: MatDialogRef<UpdateLeaveComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UpdateLeaveData
  ) {}

  ngOnInit(): void {
    this.leaveId = this.data.id;
    this.leaveBalance = this.data.balance ?? 0;
    this.leave.getById(this.leaveId).subscribe({
      next: (res: any) => {
        if (!res) return;

        const start = this.normalizeDateInput(res.fromDate);
        const end   = this.normalizeDateInput(res.toDate);

        this.form.patchValue({
          leaveType: res.leaveCategory as LeaveType,
          startDate: start,
          endDate:   end,
          duration:  res.duration ?? null,
          reason:    res.reason ?? '',
          status:    res.status ?? 'PENDING'
        });

        // normalize originals to YMD so equality checks are exact
        this.originalLeaveType   = res.leaveCategory ?? null;
        this.originalStartDateYMD = start ? this.toYMD(this.toDateOnly(start)) : null;
        this.originalEndDateYMD   = end   ? this.toYMD(this.toDateOnly(end))   : null;

        // Min date rule (2-week notice for Casual)
        const today = this.toDateOnly(new Date());
        this.minDate = res.leaveCategory === 'Casual'
          ? new Date(today.setDate(today.getDate() + 14))
          : new Date();
      },
      error: () => this.snack.open('Failed to load leave details', 'OK', { duration: 2500 })
    });

    this.form.valueChanges.subscribe(() => this.computeDuration());
    setTimeout(() => this.computeDuration());
  }

  // ---- helpers ----
  private normalizeDateInput(v: Date | string | undefined | null): Date | null {
    if (!v) return null;
    return typeof v === 'string' ? new Date(v) : v;
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

  private isWeekend(d: Date): boolean {
    const w = d.getDay();
    return w === 0 || w === 6;
  }

  private isHoliday(d: Date): boolean {
    return this.holidaySet.has(this.toYMD(d));
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
  getMaxEndDate(): Date | null {
  const start = this.form.get('startDate')?.value;
  if (start) {
    const max = new Date(start);
    max.setDate(max.getDate() + 7); // restrict to 7 days from start date
    return max;
  }
  return null;
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
    const s = this.form.get('startDate')!.value as Date | null;
    const e = this.form.get('endDate')!.value as Date | null;
    if (!s || !e) { this.durationDays = 0; return; }
    const start = this.toDateOnly(s);
    const end   = this.toDateOnly(e);
    if (end.getTime() < start.getTime()) { this.durationDays = 0; return; }
    this.durationDays = this.businessDaysBetween(start, end);
  }

  // enable only if leaveType OR start OR end changes
  hasUpdateEligibleChanges(): boolean {
    const currentType = this.form.get('leaveType')?.value ?? null;

    const s = this.form.get('startDate')?.value as Date | null;
    const e = this.form.get('endDate')?.value as Date | null;

    const currentStartYMD = s ? this.toYMD(this.toDateOnly(s)) : null;
    const currentEndYMD   = e ? this.toYMD(this.toDateOnly(e)) : null;

    const typeChanged  = currentType !== this.originalLeaveType;
    const startChanged = currentStartYMD !== this.originalStartDateYMD;
    const endChanged   = currentEndYMD   !== this.originalEndDateYMD;

    return !!(typeChanged || startChanged || endChanged);
  }

  // ---- actions ----
  submit(): void {
    if (this.form.invalid || this.durationDays <= 0) {
      this.snack.open(
        this.durationDays <= 0 ? 'No working days in selected range' : 'Please fill all required fields',
        'OK', { duration: 2500 }
      );
      return;
    }
  // Restrict leave duration to maximum 7 days
    if (this.durationDays > 7) {
      this.snack.open(
        'You cannot modify leave for more than 7 working days.',
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

    // Block completely if no balance
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

    // Block if user tries to exceed balance
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


    const start = this.toDateOnly(this.form.value.startDate!);
    const end = this.toDateOnly(this.form.value.endDate!);

    let hasOverlap = false;
    this.existingleaves.forEach((leave) => {
    const existingFrom = this.toDateOnly(leave.startDate);
    const existingTo   = this.toDateOnly(leave.endDate);
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

    const payload = {
      leaveCategory: (this.form.getRawValue().leaveType as LeaveType | null),
      fromDate: start,
      toDate: end,
      duration: this.durationDays,
      reason: this.form.value.reason,
      status: 'PENDING'
    };

    
    this.leave.update(this.leaveId, payload).subscribe({
      next: () => {
        this.snack.open('Leave Updated', 'OK', {
          duration: 2000,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
          panelClass: ['custom-snack-success'],
        });
        this.dialogRef.close(true);
      },
      error: () => this.snack.open('Update failed', 'OK', { duration: 3000 })
    });
  }

  cancelLeave(): void {
    const ref = this.dialog.open(this.cancelDialogTpl, { width: '420px', disableClose: true });
    ref.afterClosed().subscribe((confirm: boolean) => {
      if (!confirm) return; // NO clicked → do nothing
      this.leave.cancel(this.leaveId).subscribe({
        next: () => {
          this.snack.open('Leave Canceled', 'OK', {
            duration: 2000,
            horizontalPosition: 'center',
            verticalPosition: 'bottom',
            panelClass: ['custom-snack-success'],
          });
          this.dialogRef.close(true);
        },
        error: () => this.snack.open('Cancel failed', 'OK', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
          panelClass: ['custom-snack-failure'],
        })
      });
    });
  }
}
