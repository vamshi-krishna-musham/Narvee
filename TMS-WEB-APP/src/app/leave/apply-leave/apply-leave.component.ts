import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveService } from '../../services/leave.service';

@Component({
  selector: 'app-apply-leave',
  templateUrl: './apply-leave.component.html'
})
export class ApplyLeaveComponent implements OnInit {
  // datepicker minimum = today (date-only)
  minDate = this.toDateOnly(new Date());

  form = this.fb.group({
    leaveType: [null, Validators.required],
    startDate: [null, Validators.required],
    endDate:   [null, Validators.required],
    reason:    ['', [Validators.required, Validators.maxLength(500)]],
  });

  constructor(
    private fb: FormBuilder,
    private leave: LeaveService,
    private router: Router,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {}

  submit(): void {
    if (this.form.invalid) {
      this.snack.open('Please fill all required fields', 'OK', { duration: 2500 });
      return;
    }

    const start = this.toDateOnly(this.form.value.startDate!);
    const end   = this.toDateOnly(this.form.value.endDate!);

    if (end.getTime() < start.getTime()) {
      this.snack.open('End date must be on or after start date', 'OK', { duration: 3000 });
      return;
    }

    const payload = {
      leaveType: this.form.value.leaveType!,
      startDate: this.toYMD(start),  // 'yyyy-MM-dd'
      endDate:   this.toYMD(end),    // 'yyyy-MM-dd'
      reason:    (this.form.value.reason || '').trim()
    };

    this.leave.apply(payload).subscribe({
      next: () => {
        this.snack.open('Leave submitted', 'OK', { duration: 2000 });
        this.router.navigate(['/leave/history']);
      },
      error: (e) => {
        const msg = e?.error?.message || 'Submission failed';
        this.snack.open(msg, 'OK', { duration: 3500 });
      }
    });
  }

  // ----- helpers -----
  private toDateOnly(d: string | Date): Date {
    const date = typeof d === 'string' ? new Date(d) : d;
    return new Date(date.getFullYear(), date.getMonth(), date.getDate()); // local midnight
  }

  private toYMD(d: Date): string {
    const y = d.getFullYear();
    const m = `${d.getMonth() + 1}`.padStart(2, '0');
    const day = `${d.getDate()}`.padStart(2, '0');
    return `${y}-${m}-${day}`;
  }
}
