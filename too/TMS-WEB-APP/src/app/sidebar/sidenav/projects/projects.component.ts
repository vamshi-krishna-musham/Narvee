import { Component, inject, TemplateRef, ViewChild } from '@angular/core';
import { AddProjectComponent } from '../../add-project/add-project.component';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatPaginatorIntl, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { CommonModule } from '@angular/common';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ProjectsService } from '../../../services/projects.service';
import { Router } from '@angular/router';
import { ISnackBarData, SnackbarService, } from '../../../PathService/snack-bar.service';
import { Subject, take, takeUntil } from 'rxjs';
import { DialogService } from '../../../PathService/dialog.service';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { MatDialog, MatDialogConfig, MatDialogModule } from '@angular/material/dialog';
import { Project } from '../project-model';
import { IConfirmDialogData } from './confirm-dialog-data';
import { ConfirmdeleteComponent } from '../../confirmdelete/confirmdelete.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonDeleteComponent } from '../../common-delete/common-delete.component';
import { ApiserviceService } from '../../../PathService/apiservice.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-projects',
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
            MatDialogModule,
    
  ],
  templateUrl: './projects.component.html',
  styleUrl: './projects.component.scss'
})
export class ProjectsComponent {
  private projectServ = inject(ProjectsService);
  dataSource = new MatTableDataSource<any>([]);

  fileDisplayedColumns: string[] = ['icon', 'fileName', 'view', 'download'];

  constructor(private dialog: MatDialog,private apiService: ApiserviceService,private http: HttpClient){}
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
  private router = inject(Router);
  private snackBarServ = inject(SnackbarService);
  // to clear subscriptions
  private destroyed$ = new Subject<void>();
  dataTobeSentToSnackBarService: ISnackBarData = {
    message: '',
    duration: 2500,
    verticalPosition: 'top',
    horizontalPosition: 'center',
    direction: 'above',
    panelClass: ['custom-snack-success'],
  };
 
  private breakpointObserver = inject(BreakpointObserver);
  canAddProject: boolean = true;
  canEditProject: boolean = false;
canDeleteProject: boolean = false;
canSeeProjectActions: boolean = false;
dataTableColumns: string[] = []; // Move initialization to ngOnInit

ngOnInit(): void {

  const privileges = JSON.parse(localStorage.getItem('rolePrivileges') || '[]');
  this.canAddProject = privileges.includes('CREATE_PROJECT'); 
  this.canEditProject = privileges.includes('EDIT_PROJECT');
  this.canDeleteProject = privileges.includes('DELETE_PROJECT');
  this.canSeeProjectActions = privileges.includes('PROJECT_ACTION_VISIBLE');
  this.dataTableColumns = [
    'SerialNum',
    'projectId',
    'projectName',
    'AssignedTo',
    'StartDate',
    'DueDate',
    'status',
    'AddedBy',
    ...(this.canSeeProjectActions ? ['Action'] : []),
    'ViewFiles',
    'MoreInfo'
  ];
  this.getprojects()
}



 
  showComments(row: any) {
    // Logic to show comments (can be dialog, bottom sheet, etc.)
    console.log('Show comments for project:', row.projectName);
  }
  

  addProject() {
    const actionData = {
      title: 'Add Project',
      ProjectData: null,
      actionName: 'add-project',
    };
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.panelClass = 'add-project';
    dialogConfig.data = actionData;
  dialogConfig.width = '750px';

    this.breakpointObserver.observe([
      Breakpoints.XSmall,
      Breakpoints.Small,
      Breakpoints.Medium,
      Breakpoints.Large,
      Breakpoints.XLarge
    ]).pipe(
      take(1)
    ).subscribe(result => {
      if (result.matches) {
        if (result.breakpoints[Breakpoints.XSmall]) {
          dialogConfig.width = '90vw';
        } else if (result.breakpoints[Breakpoints.Small]) {
          dialogConfig.width = '80vw';
        } else if (result.breakpoints[Breakpoints.Medium]) {
          dialogConfig.width = '70vw';
        } else if (result.breakpoints[Breakpoints.Large]) {
          dialogConfig.width = '60vw';
        } else if (result.breakpoints[Breakpoints.XLarge]) {
          dialogConfig.width = '60vw';
        }
      }

      const dialogRef = this.dialogServ.openDialogWithComponent(AddProjectComponent, dialogConfig);
      dialogRef.afterClosed().subscribe((result) => {
        if (result === true) {
          this.getprojects();
        }
      });
      
    });

  }
  getprojects(pagIdx = 1) {
    const profileRole = localStorage.getItem('profileRole');
    const profileId = localStorage.getItem('profileId');
    const pagObj = {
      pageNumber: pagIdx,
      pageSize: this.itemsPerPage,
      sortField: this.sortField,
      sortOrder: this.sortOrder,
      keyword: this.field || 'empty', // search keyword
      access:profileRole,
     userid:profileId
    };
  
    return this.projectServ.getprojectdetails(pagObj)
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (response: any) => {
          this.totalItems = response.data.totalElements || 0;
          this.dataSource.data = response.data?.content.map((x: any, i: number) => ({
            ...x,
            serialNum: (pagIdx - 1) * this.itemsPerPage + i + 1
          }));
          console.log(response.data,'this.dataSource.data');
          
        },
        
        error: (err: any) => {
          console.log('errorrr');

          this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
          this.dataTobeSentToSnackBarService.message =
            err?.message || 'Failed to fetch team members.';
          this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
        }
      });
  }
  searchTimeout: any;

  handlePageEvent(event:PageEvent){
    this.pageSize = event.pageSize;
    this.itemsPerPage = event.pageSize;
    this.currentPageIndex = event.pageIndex;
    this.getprojects(this.currentPageIndex + 1); // 1-based index for API
  }
  sortData(event: Sort) {
    this.sortField = event.active;
    this.sortOrder = event.direction || 'asc';
    this.getprojects(this.currentPageIndex + 1);
  }
  onSearchChange() {
    clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => {
      this.currentPageIndex = 0; // Reset to first page when searching
      this.getprojects(1);
    }, 300); 
  }
  editProject(project: any) {
    const actionData = {
      title: 'Update Project',
      ProjectData: project,
      actionName: 'edit-project',
    };
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.panelClass = 'edit-project';
    dialogConfig.data = actionData;
    dialogConfig.width = '750px'; // Set fixed width

    this.breakpointObserver.observe([
      Breakpoints.XSmall,
      Breakpoints.Small,
      Breakpoints.Medium,
      Breakpoints.Large,
      Breakpoints.XLarge
    ]).pipe(
      take(1)
    ).subscribe(result => {
      if (result.matches) {
        if (result.breakpoints[Breakpoints.XSmall]) {
          dialogConfig.width = '90vw';
        } else if (result.breakpoints[Breakpoints.Small]) {
          dialogConfig.width = '80vw';
        } else if (result.breakpoints[Breakpoints.Medium]) {
          dialogConfig.width = '70vw';
        } else if (result.breakpoints[Breakpoints.Large]) {
          dialogConfig.width = '60vw';
        } else if (result.breakpoints[Breakpoints.XLarge]) {
          dialogConfig.width = '60vw';
        }
      }
    
      const dialogRef = this.dialogServ.openDialogWithComponent(AddProjectComponent, dialogConfig);
      dialogRef.afterClosed().subscribe((result) => {
        if (result === true) {
          this.getprojects();
        }
      });
      
    });
  }

  deleteProject(project: any) {
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
          this.projectServ.deleteProject(project.pid).pipe(takeUntil(this.destroyed$)).subscribe({
            next: (response: any) => {
              if (response.status === 'success') {
                this.getprojects();
                this.dataTobeSentToSnackBarService.message= response.message || 'Project Deleted Successfully';
                this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-success'];
              } else {
                this.dataTobeSentToSnackBarService.message= response.message || 'Project Deletion Failed';
                this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
              }
              this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
            },
            error: (err: any) => {
              this.dataTobeSentToSnackBarService.message= err.message || 'Project Deleted Failed';;
              this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
              this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
            },
          });
        }
      },
    });
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
  

  // applyFilter(event: any) {
  //   const keyword = event.target.value;
  //   this.field = keyword;
  //   if (keyword != '') {
  //     const pagObj = {
  //       pageNumber: 1,
  //       pageSize: this.itemsPerPage,
  //       sortField: this.sortField,
  //       sortOrder: this.sortOrder,
  //       keyword: this.field,
  //       access: this.hasAcces,
  //       userid: this.userid
  //     }
  //     return this.projectServ.getAllProjectsWithPaginationAndSorting(pagObj).subscribe({
  //       next: (response: any) => {
  //         this.entity = response.data.content;
  //         this.dataSource.data = response.data.content;
  //         this.totalItems = response.data.totalElements;
  //         this.dataSource.data.map((x: any, i) => {
  //           x.serialNum = i + 1;
  //         });
  //       },
  //       error: (err: any) => {
  //         this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
  //         this.dataTobeSentToSnackBarService.message = err.message;
  //         this.snackBarServ.openSnackBarFromComponent(
  //           this.dataTobeSentToSnackBarService
  //         );
  //       }
  //     }
  //     );
  //   }
  //   if (keyword == '') {
  //     this.field = 'empty';
  //   }
  //   return this.getAll(this.currentPageIndex + 1);
  // }

  // onSort(event: any) {
  //   if (event.active == 'SerialNum')
  //     this.sortField = 'postedon'
  //   else
  //     this.sortField = event.active;
  //   this.sortOrder = event.direction;

  //   if (event.direction != '') {
  //     this.getAll();
  //   }
  // }

  // generateSerialNumber(index: number): number {
  //   const pagIdx = this.currentPageIndex === 0 ? 1 : this.currentPageIndex + 1;
  //   const serialNumber = (pagIdx - 1) * 50 + index + 1;
  //   return serialNumber;
  // }

  // handlePageEvent(event: PageEvent) {
  //   if (event) {
  //     this.pageEvent = event;
  //     this.currentPageIndex = event.pageIndex;
  //     this.getAll()
  //   }
  //   return;
  // }

  // ngOnDestroy(): void {
  //   this.destroyed$.next(undefined);
  //   this.destroyed$.complete()
  // }

  // openTasks(projectId: string, pid: string | number): void {
  //   this.projectServ.setPid(pid);
  //   this.router.navigate([`/task-management/projects/${projectId}/tasks`]);
  // }
addtask(projectId: string, pid: number, targetDate: string ,startDate:string ,status:string,projectname:string) {
  console.log(projectId, 'user clicked project');
  console.log(pid, 'pid');
  console.log(targetDate, 'target date');
  console.log(status,'statusss');
  console.log(projectname,'projectname');
  
  
  this.router.navigate(['/addtask'], {
    queryParams: {
      projectId: projectId,
      pid: pid,
      targetDate: targetDate,
      startDate:startDate,
      status:status,
      projectname:projectname
    }
  });
}

  statusOptions = [
    { label: 'To Do', value: 'To Do', color: '#FF9800' },
    { label: 'In Progress', value: 'In Progress', color: '#2196F3' },
    { label: 'On Hold', value: 'On Hold', color: '#9E9E9E' },
    { label: 'In Review', value: 'In Review', color: '#673AB7' },
    { label: 'Completed', value: 'Completed', color: '#4CAF50' }
  ];
  
  
  getStatusStyles(status: string) {
    const matchedStatus = this.statusOptions.find(opt => opt.value === status);
    if (matchedStatus) {
      const bgColor = matchedStatus.color + '20'; // e.g., #4CAF5020
      return {
        color: matchedStatus.color,
        'background-color': bgColor,
        padding: '4px 8px',
        'border-radius': '12px',
        'font-weight': '500',
        'font-size': '13px'
      };
    }
    return {};
  }
  // getDisplayNames(users: any[]): string {
  //   if (!users || users.length === 0) return '-';
  //   const names = users.slice(0, 2).map(u => u.fullname);
  //   return names.join(', ') + (users.length > 2 ? ', ...' : '');
  // }
    selectedFiles: any[] = [];
    @ViewChild('filesDialog') filesDialog!: TemplateRef<any>;
  openFilesDialog(files: any[]): void {
    this.selectedFiles = files;
    this.dialog.open(this.filesDialog);
  }
  downloadFile(file: any) {
    this.projectServ.downloadfile(file.id).subscribe((blob: Blob) => {
      const fileName = file.fileName.split('-')[0];
      const downloadUrl = window.URL.createObjectURL(blob);
      
      const a = document.createElement('a');
      a.href = downloadUrl;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      
      window.URL.revokeObjectURL(downloadUrl);
    });
  }
  
closeDialog() {
  this.dialog.closeAll();
}
  @ViewChild('previewDialog') previewDialog!: TemplateRef<any>;

  previewUrl: string = '';
  previewType: 'image' | 'text' = 'image';

  viewFile(file: any): void {
    const url = `${this.apiService.apiUrl}task/project/download-file/${file.id}`;
  
    this.http.get(url, { responseType: 'blob' }).subscribe(blob => {
      const fileType = blob.type;
      const blobUrl = URL.createObjectURL(blob);
  
      if (fileType === 'application/pdf') {
        window.open(blobUrl, '_blank');
      } else if (fileType.startsWith('image/')) {
        this.previewUrl = blobUrl;
  
        this.dialog.open(this.previewDialog, {
          width: '60%',
          maxWidth: '50vw',
          height: 'auto',
          panelClass: 'custom-image-dialog'
        });
        
      } else if (fileType === 'text/plain') {
        const newWindow = window.open(blobUrl, '_blank');
      } else {
        const newWindow = window.open();
        newWindow?.document.write(`
          <html>
            <head>
              <title>No Preview</title>
              <style>
                body {
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  height: 100vh;
                  margin: 0;
                  background-color:rgb(37, 36, 36);
                  font-family: Arial, sans-serif;
                }
                h2 {
                  color: white;
                }
              </style>
            </head>
            <body>
              <h2>No preview available for this file type.</h2>
            </body>
          </html>
        `);
      }
    }, error => {
      console.error('File view failed', error);
    });
  }
  
  
  
  @ViewChild('descriptionDialog') descriptionDialog!: TemplateRef<any>;

  showDescription(row: any): void {
    const description = row.projePage?.projectdescription || 'No description available';
    
    this.dialog.open(this.descriptionDialog, {
      data: { description },
      width: '80%'
    });
  }
  projects: { assignUsers: { fullname: string }[] }[] = [];

  // This method returns the first capitalized letter of the fullname
  getInitial(user: any): string {
    return user?.fullname?.charAt(0)?.toUpperCase() || '';
  }
  
  

  getDisplayNames(users: { fullname: string }[]): string {
    if (!users || users.length === 0) return '-';
    return users.map(u => u.fullname).join(', ');
  }
  
  getAvatarColor(index: number): string {
    const colors = ['#f44336', '#2196f3', '#4caf50', '#ff9800', '#9c27b0'];
    return colors[index % colors.length];
  }

  getColor(index: number): string {
    const colors = ['#4caf50', '#2196f3', 'rgb(223 59 215) ', '#9c27b0', '#f44336'];
    return colors[index % colors.length];
  }
  
}
