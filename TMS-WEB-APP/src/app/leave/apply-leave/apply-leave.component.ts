import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveService,LeaveRequest } from '../../services/leave.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-apply-leave',
  templateUrl: './apply-leave.component.html',
  styleUrls: ['./apply-leave.component.scss']
})
export class ApplyLeaveComponent implements OnInit {
  minDate = this.toDateOnly(new Date());
  durationDays = 0;

  private holidaysYMD: string[] = [];
  private holidaySet = new Set(this.holidaysYMD);

  form = this.fb.group({
    leaveType: [null, Validators.required],
    startDate: [null, Validators.required],
    endDate: [null, Validators.required],
    reason: ['', [Validators.required, Validators.maxLength(500)]],
  });
  existingleaves: LeaveRequest[] = [];

  constructor(
    private fb: FormBuilder,
    private leave: LeaveService,
    private snack: MatSnackBar,
    private router: Router
  ) {}


  ngOnInit(): void {

    this.form.get('leaveType')?.valueChanges.subscribe(type => {
      const today= this.toDateOnly(new Date());
      if (type === 'Casual') {
        this.minDate = new Date(today.setDate(today.getDate() + 14));
      }
       else {
        // default (today)
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
      next: (data) => {
        this.existingleaves = data;
      },
      error: (err) => {
        this.snack.open(err.error?.message || 'Failed to load leaves', 'OK', { duration: 3000 });
      }
    });
  }

  private computeDuration(): void {
    const s = this.form.get('startDate')!.value as Date | string | null;
    const e = this.form.get('endDate')!.value as Date | string | null;

    if (!s || !e) { this.durationDays = 0; return; }

    const start = this.toDateOnly(s);
    const end = this.toDateOnly(e);
    if (end.getTime() < start.getTime()) { this.durationDays = 0; return; }

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

  submit(): void {
    if (this.form.invalid || this.durationDays <= 0) {
      this.snack.open(
        this.durationDays <= 0 ? 'No working days in selected range' : 'Please fill all required fields',
        'OK', { duration: 2800 }
      );
      return;
    }

    const start = this.toDateOnly(this.form.value.startDate!);
    const end   = this.toDateOnly(this.form.value.endDate!);
    let flag=false;
    const duration = this.durationDays;
    const payload = {
      userId: Number(localStorage.getItem('profileId')),
      userName: ((localStorage.getItem('firstName') || '') + ' ' + (localStorage.getItem('lastName') || '')) || 'Unknown',
      leaveCategory: this.form.value.leaveType,
      fromDate: this.toYMD(start),
      toDate: this.toYMD(end),
      reason: (this.form.value.reason || '').trim(),
      status: 'PENDING',
      duration: duration
    };
    this.existingleaves.forEach(leave => {
      const existingFrom = this.toDateOnly(leave.startDate);
      const existingTo = this.toDateOnly(leave.endDate); 
      const existingstatus= leave.status 
      // check for overlap
      if (
      (start >= existingFrom && start <= existingTo && (existingstatus === 'APPROVED' || existingstatus === 'PENDING' || existingstatus === 'REJECTED')) ||
      (end >= existingFrom && end <= existingTo && (existingstatus === 'APPROVED' || existingstatus === 'PENDING' || existingstatus === 'REJECTED')) ||
      (start <= existingFrom && end >= existingTo && (existingstatus === 'APPROVED' || existingstatus === 'PENDING' || existingstatus === 'REJECTED'))
      ){
        flag=true;
      }
    });
    if(flag){
          this.snack.open('Leave dates overlap with existing pending leave or approved leave', 'OK', { 
          duration: 3000,
          horizontalPosition: 'center',  
          verticalPosition: 'top',
          panelClass: ['custom-snack-failure']
        });
    }
    else{
      this.leave.apply(payload as any).subscribe({
      next: () => {
        this.snack.open('Leave submitted successfully', 'OK', {
          duration: 2500,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
          panelClass: ['custom-snack-success']   // or 'snack-light'
        });
        this.router.navigate(['/leave/history']); // ✅ go back to history after success
      },
      error: e => {
        this.snack.open(e.error?.message || 'Submission failed', 'OK', {
          duration: 3500,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
          panelClass: ['custom-snack-failure']});
      }
    });
    }
    

  }

  cancel(): void {
    this.router.navigate(['/leave/history']); // ✅ cancel → back to history
  }
}
