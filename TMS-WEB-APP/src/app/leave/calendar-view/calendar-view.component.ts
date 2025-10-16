import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { LeaveService, LeaveRequest } from '../../services/leave.service';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'calendar-view',
  templateUrl: './calendar-view.component.html'
})
export class CalendarViewComponent implements OnInit {
  leaves: LeaveRequest[] = [];

  constructor(
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private leave: LeaveService
  ) {}
  displayedColumns = ['no','name','startdate', 'enddate','duration'];
  
    dataSource = new MatTableDataSource<LeaveRequest>([]);
    @ViewChild(MatPaginator) paginator!: MatPaginator;
  
  ngOnInit(): void {
    this.leave.listApproved(1).subscribe({
      next: (res) => {
        this.leaves = res;
        this.dataSource.data = res;
        console.log('Approved leaves:', this.leaves);
      },
      error: (err) => console.error('Error loading leaves', err)
    });
  }
}