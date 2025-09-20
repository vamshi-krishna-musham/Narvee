import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ISnackBarData, SnackbarService } from '../../PathService/snack-bar.service';
import { Subject, takeUntil } from 'rxjs';
import { ProjectsService } from '../../services/projects.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AddTeamMemberComponent } from '../sidenav/add-team-member/add-team-member.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IConfirmDialogData } from '../sidenav/projects/confirm-dialog-data';
import { DialogService } from '../../PathService/dialog.service';
import { CommonDeleteComponent } from '../common-delete/common-delete.component';

@Component({
  selector: 'app-team-member',
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
      FormsModule
    ],
  templateUrl: './team-member.component.html',
  styleUrl: './team-member.component.scss'
})
export class TeamMemberComponent {
  private destroyed$ = new Subject<void>();
  totalItems: any;
  pageEvent!: PageEvent;
  pageSize = 10;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  pageSizeOptions = [5, 10, 25];
  currentPageIndex = 0;
  itemsPerPage = 10;
  sortField = 'updateddate';
  sortOrder = 'desc';
  field = "";
  private dialogServ = inject(DialogService);

  constructor(private dialog: MatDialog) {}

    dataTobeSentToSnackBarService: ISnackBarData = {
      message: '',
      duration: 2500,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      direction: 'above',
      panelClass: ['custom-snack-success'],
    };
      private snackBarServ = inject(SnackbarService);
      private projectServ = inject(ProjectsService);
    
  dataSource = new MatTableDataSource<any>([]);
  dataTableColumns: string[] = [
    'SerialNum',
    'userName',
    'email',
    'PhoneNumber',
    'Position',
   'RoleName',
   'AddedBy',
   'Action'
  ];
  canAddProject: boolean = true;
  canEditProject: boolean = false;
canDeleteProject: boolean = false;
canSeeProjectActions: boolean = false;
  ngOnInit(){
    const privileges = JSON.parse(localStorage.getItem('rolePrivileges') || '[]');
    this.canAddProject = privileges.includes('CREATE_TEAM_MEMBER'); 
    this.canEditProject = privileges.includes('EDIT_TEAM_MEMBER');
    this.canDeleteProject = privileges.includes('DELETE_TEAM_MEMBER');
    this.canSeeProjectActions = privileges.includes('TEAM_MEMBER_ACTION_VISIBLE');
    this.addteammember()
  }
  openAddProjectDialog(): void {
    const dialogRef = this.dialog.open(AddTeamMemberComponent, {
      width: '100%',
      maxWidth: '650px',
      disableClose: true,
    });
  
    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.addteammember(this.currentPageIndex + 1); // refresh table data
      }
    });
  }
  

  
  addteammember(pagIdx = 1) {
    const profileId = localStorage.getItem('profileId');
    const adminId=localStorage.getItem('adminId')
    const pagObj = {
      pageNumber: pagIdx,
      pageSize: this.itemsPerPage,
      sortField: this.sortField,
      sortOrder: this.sortOrder,
      keyword: this.field || 'empty', 
      adminId: adminId || null,
      profileId:profileId || null
    };
  
    return this.projectServ.getuserdetails(pagObj)
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (response: any) => {
          this.totalItems = response.data.totalElements || 0;
          this.dataSource.data = response.data.content.map((x: any, i: number) => ({
            ...x,
            serialNum: (pagIdx - 1) * this.itemsPerPage + i + 1
          }));
        },
        error: (err: any) => {
          this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
          this.dataTobeSentToSnackBarService.message =
            err?.error?.message || 'Failed to fetch team members.';
          this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
        }
      });
  }
  searchTimeout: any;

onSearchChange() {
  clearTimeout(this.searchTimeout);
  this.searchTimeout = setTimeout(() => {
    this.currentPageIndex = 0;
    this.addteammember(1);
  }, 300); 
}

handlePageEvent(event: PageEvent) {
  this.pageSize = event.pageSize;
  this.itemsPerPage = event.pageSize;
  this.currentPageIndex = event.pageIndex;
  this.addteammember(this.currentPageIndex + 1);
}

sortData(event: Sort) {
  this.sortField = event.active;
  this.sortOrder = event.direction || 'asc';
  this.addteammember(this.currentPageIndex + 1);
}
  private getDialogConfigData(
    dataToBeSentToDailog: Partial<IConfirmDialogData>,
    action: { delete: boolean; edit: boolean; add: boolean; updateSatus?: boolean }
  ) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = action.edit || action.add ? '40vw' : action.delete ? '600px' : '400px'; // ⬅️ increased from 'fit-content' to '600px'
    dialogConfig.maxHeight = '90vh'; // ⬅️ limit max height to avoid unnecessary scroll
    dialogConfig.autoFocus = false;
    dialogConfig.disableClose = false;
    dialogConfig.panelClass = dataToBeSentToDailog.actionName;
    dialogConfig.data = dataToBeSentToDailog;
    return dialogConfig;
  }
deleteTeamMember(teamMember:any){
  const dataToBeSentToDailog: Partial<IConfirmDialogData> = {
        title: 'Confirmation',
        message: 'Are you sure you want to delete?',
        confirmText: 'Yes',
        cancelText: 'No',
        actionName: 'delete-Project'
      };
  
      const dialogConfig = this.getDialogConfigData(dataToBeSentToDailog, { delete: true, edit: false, add: false });
  
      const dialogRef = this.dialogServ.openDialogWithComponent(
        CommonDeleteComponent,
        dialogConfig
      );
  
      dialogRef.afterClosed().subscribe({
        next: () => {
          if (dialogRef.componentInstance.allowAction) {
            this.projectServ.DeleteTeamMember(teamMember.userId).pipe(takeUntil(this.destroyed$)).subscribe({
              next: (response: any) => {
                if (response.status === 'success') {
                  this.addteammember();
                  console.log('deleteteee');
                  console.log(response.message,'messageee');
                  
                  this.dataTobeSentToSnackBarService.message= response.message || 'Team Member Deleted Successfully';
                  this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-success'];
                } else {
                  this.dataTobeSentToSnackBarService.message= response.message || 'Team Member Deletion Failed';
                  this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
                }
                this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
              },
              error: (err: any) => {
                this.dataTobeSentToSnackBarService.message= err.message || 'Team Member Deletion Failed';
                this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
                this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
              },
            });
          }
        },
      });
}
editTeamMember(element: any) {
  const dialogRef = this.dialog.open(AddTeamMemberComponent, {
    width: '100%',
    maxWidth: '650px',
    disableClose: true,
    data: element // pass the data
  });

  dialogRef.afterClosed().subscribe((result) => {
    if (result === true) {
      this.addteammember(this.currentPageIndex + 1); // refresh table data
    }
  });
}

}
