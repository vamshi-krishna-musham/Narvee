import { Component, OnInit,TemplateRef,ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveService } from '../../services/leave.service';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';


@Component({
  selector: 'app-update-leave',
  templateUrl: './update-leave.component.html',
  styleUrls: ['./update-leave.component.scss']
})
export class UpdateLeaveComponent implements OnInit {
  @ViewChild('cancelDialog') cancelDialogTpl!: TemplateRef<any>;
  form = this.fb.group({
    leaveType: [null, Validators.required],
    startDate: [null, Validators.required],
    endDate: [null, Validators.required],
    duration: [null],
    reason: ['', [Validators.required, Validators.maxLength(500)]],
    adminComment: [''],
    status: ['PENDING', Validators.required]
  });
  

  leaveId!: number;

  constructor(
    private fb: FormBuilder,
    private leave: LeaveService,
    private route: ActivatedRoute,
    private snack: MatSnackBar,
    private router: Router,
    private dialog: MatDialog
  ) {}
  originalStartDate: string | null = null;
  originalEndDate: string | null = null;

  ngOnInit(): void {
    // Get leave ID from URL
    this.leaveId = Number(this.route.snapshot.paramMap.get('id'));
    console.log('Received Leave ID:', this.leaveId);  // ✅ verify here
    console.log('Type of Leave ID:', typeof this.leaveId);  // ✅ verify type
    

    // Fetch existing leave data
    this.leave.getById(this.leaveId).subscribe({
      next: (res: any) => {
        console.log('Leave Data:', res);
        if (res) {
          this.form.patchValue({
            leaveType: res.leaveCategory,
            startDate: res.fromDate,
            endDate: res.toDate,
            duration: res.duration,
            reason: res.reason,
            adminComment: res.adminComment,
            status: res.status
          });
        this.originalStartDate = res.fromDate;
        this.originalEndDate = res.toDate;
        }
      },
      error: () => this.snack.open('Failed to load leave details', 'OK', { duration: 2500 })
    });
  }
  datesChanged(): boolean {
  const start = this.form.get('startDate')?.value;
  const end = this.form.get('endDate')?.value;
  return start !== this.originalStartDate || end !== this.originalEndDate;
}


  submit(): void {
    if (this.form.invalid) {
      this.snack.open('Please fill all required fields', 'OK', { duration: 2500 });
      return;
    }

    const payload = {
      leaveCategory: this.form.value.leaveType,
      fromDate: this.form.value.startDate,
      toDate: this.form.value.endDate,
      duration: this.form.value.duration,
      reason: this.form.value.reason,
      adminComment: this.form.value.adminComment,
      status: "PENDING"  // Always reset to PENDING on update
    };

    this.leave.update(this.leaveId, payload).subscribe({
      next: () => {
      this.snack.open('Leave Updated', 'OK', {
                  duration: 2000, horizontalPosition: 'center', verticalPosition: 'bottom',
                  panelClass: ['custom-snack-success']
                });        
      this.router.navigate(['/leave/history']);
      },
      error: () => this.snack.open('Update failed', 'OK', { duration: 3000 })
    });
  }
  exit(): void {
    this.router.navigate(['/leave/history']);
  }
  cancel(id: number): void {
    const profileId = Number(localStorage.getItem('profileId'));
    const ref = this.dialog.open(this.cancelDialogTpl, {
      width: '420px',
      disableClose: true,
      data: { comment: '' }
    });
ref.afterClosed().subscribe(comment => {
      if (!comment) return;
      this.leave.cancel(id).subscribe({
        next: () => {
          this.snack.open('Leave Canceled', 'OK', {
            duration: 2000, horizontalPosition: 'center', verticalPosition: 'bottom',
            panelClass: ['custom-snack-success']
          });
          this.router.navigate(['/leave/history']);
        },
        error: () => this.snack.open('Deny failed', 'OK', {
          duration: 3000, horizontalPosition: 'center', verticalPosition: 'bottom',
          panelClass: ['custom-snack-failure']
        })
      });
    });
  }
}
