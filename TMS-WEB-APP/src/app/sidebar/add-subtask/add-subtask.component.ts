import { CommonModule } from '@angular/common';
import { Component, inject, TemplateRef, ViewChild } from '@angular/core';
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
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { AddTeamMemberComponent } from '../sidenav/add-team-member/add-team-member.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AddTaskviewComponent } from '../add-taskview/add-taskview.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AddSubtaskviewComponent } from '../add-subtaskview/add-subtaskview.component';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { IConfirmDialogData } from '../sidenav/projects/confirm-dialog-data';
import { DialogService } from '../../PathService/dialog.service';
import { CommonDeleteComponent } from '../common-delete/common-delete.component';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClient } from '@angular/common/http';
import { ApiserviceService } from '../../PathService/apiservice.service';

@Component({
  selector: 'app-add-subtask',
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
    MatSelectModule,
    MatOptionModule,
    MatDialogModule
  ],
  templateUrl: './add-subtask.component.html',
  styleUrl: './add-subtask.component.scss'
})
export class AddSubtaskComponent {
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
  canAddProject: boolean = true;
  canEditProject: boolean = false;
  canDeleteProject: boolean = false;
  canSeeProjectActions: boolean = false;
  private dialogServ = inject(DialogService);
  fileDisplayedColumns: string[] = ['icon', 'fileName', 'view', 'download'];

  constructor(private dialog: MatDialog, private router: Router, private route: ActivatedRoute, private apiService: ApiserviceService, private http: HttpClient) { }

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
  dataSource = new MatTableDataSource<any>([

  ]);
  dataTableColumns: string[] = []; // Move initialization to ngOnInit



  priorityList = [
    { label: 'High', value: 'High', color: '#f44336' },       // Red
    { label: 'Medium', value: 'Medium', color: '#ff9800' },   // Orange
    { label: 'Low', value: 'Low', color: '#4caf50' },         // Green
    { label: 'None', value: 'None', color: '#9e9e9e' }         // Grey
  ];

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
  statusOptions = [
    { label: 'Open', value: 'Open', color: '#4caf50' },
    { label: 'In Progress', value: 'In Progress', color: '#2196f3' },
    { label: 'In Review', value: 'In Review', color: '#ff9800' },
    { label: 'To be Tested', value: 'To be Tested', color: '#9c27b0' },
    { label: 'On Hold', value: 'On Hold', color: '#795548' },
    { label: 'Overdue', value: 'Overdue', color: '#f44336' },
    { label: 'Closed', value: 'Closed', color: '#607d8b' },
    { label: 'Blocked', value: 'Blocked', color: '#e91e63' },
  ];
  getStatusColor(status: string): string {
    const found = this.statusOptions.find(opt => opt.value === status);
    return found ? found.color : '#ccc';
  }
  setEditingRow(id: number): void {
    this.editingStatusId = id;
  }

  getStatusClass(status: string) {
    const statusObj = this.statusList.find(s => s.value === status);
    return statusObj ? statusObj.color : '#9e9e9e'; // Default grey if status not found
  }

  getPriorityClass(priority: string) {
    const priorityObj = this.priorityList.find(p => p.value === priority);
    return priorityObj ? priorityObj.color : '#9e9e9e'; // Default grey if priority not found
  }
  goBack(): void {
    this.router.navigate(['/addtask'], {
      queryParams: {
        projectId: this.projectId,
        pid: this.pid ,
       projectname  :this.projectname
      }
    });
  }
    previewDialogRef!: MatDialogRef<any>;
  
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


  closeDialog(): void {
    if (this.previewDialogRef) {
      this.previewDialogRef.close();
    }
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

        this.previewDialogRef = this.dialog.open(this.previewDialog, {
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
  projectId!: string;
  pid!: any
  taskid!: any
  ticketid: any
  targetdate: any
  startdate:any
  status:any
  projectStatus:any
  taskname:any
  projectname:any
  ngOnInit() {
    const privileges = JSON.parse(localStorage.getItem('rolePrivileges') || '[]');
    this.canAddProject = privileges.includes('CREATE_SUB_TASK');
    this.canEditProject = privileges.includes('EDIT_SUB_TASK');
    this.canDeleteProject = privileges.includes('DELETE_SUB_TASK');
    this.canSeeProjectActions = privileges.includes('SUB_TASK_ACTION_VISIBLE');
    this.dataTableColumns = [
      'SerialNum',
      'SubTaskId',
      'SubTaskName',
      'AssignedTo',
      'Status',
      'StartDate',
      'Priority',
      'DueDate',
      'Duration',
      'UpdatedBy',
      ...(this.canSeeProjectActions ? ['Action'] : []),
      'ViewFiles',
      'MoreInfo'
    ];
    this.route.queryParams.subscribe(params => {
      this.taskid = params['taskid'];
      this.projectId = params['projectId'];
      this.pid = params['pid'];
      this.ticketid = params['ticketid']
      this.targetdate = params['targetdate']
      this.status =params['status']
       this.projectStatus=params['projectStatus']
       this.taskname = params['taskName']
       this.projectname = params['projectname']
this.startdate=params['startdate']
      console.log(this.targetdate, 'this.targetdatengOnInit');
      console.log(this.startdate, 'this.startdatedatengOnInit');

      console.log(this.taskid, 'taskid from route');
      console.log(this.projectId, 'projectId from route');
      console.log(this.pid, 'pid from route');
      console.log(this.status,'statusssss');
      console.log(this.projectStatus,'projectStatus');
      console.log(this.taskname,'taskname');
      console.log(this.projectname,'projectname');
      
      
      
    });
    this.getsubTaskDetailsAll()

  }
addsubtaskview() {
  if (this.projectStatus === 'Completed') {
    const snackBarData: ISnackBarData = {
      message: 'Cannot add a subtask. Project is completed.',
      duration: 3000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-failure'],
    };
    this.snackBarServ.openSnackBarFromComponent(snackBarData);
    return; // Prevent dialog from opening
  }

  if (this.status === 'Closed') {
    const snackBarData: ISnackBarData = {
      message: 'Cannot add a subtask. Task  is closed.',
      duration: 3000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-failure'],
    };
    this.snackBarServ.openSnackBarFromComponent(snackBarData);
    return; // Prevent dialog from opening
  }

  const dialogRef = this.dialog.open(AddSubtaskviewComponent, {
    width: '100%',
    maxWidth: '700px',
    disableClose: true,
    data: {
      projectId: this.projectId,
      pid: this.pid,
      taskid: this.taskid,
      targetdate: this.targetdate,
      startdate: this.startdate
    }
  });

  dialogRef.afterClosed().subscribe((result) => {
    if (result === true) {
      this.getsubTaskDetailsAll(this.currentPageIndex + 1);
    }
  });
}

  edittaskview(task: any) {
    const dialogRef = this.dialog.open(AddSubtaskviewComponent, {
      width: '100%',
      maxWidth: '700px',
      disableClose: true,
      data: {
        projectId: this.projectId,
        pid: this.pid,
        taskid: this.taskid,
        targetdate: this.targetdate,
       startdate:this.startdate,

        subtaskId: task.subTaskId

      }

    });
    console.log();

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.getsubTaskDetailsAll(this.currentPageIndex + 1); // refresh table data
      }
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
  deleteTask(task: any) {
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
          this.projectServ.deletesubTask(task.subTaskId).pipe(takeUntil(this.destroyed$)).subscribe({
            next: (response: any) => {
              if (response.status === 'success') {
                this.getsubTaskDetailsAll();
                this.dataTobeSentToSnackBarService.message = response.message || 'Sub Task Deleted Successfully';
                this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-success'];
              } else {
                this.dataTobeSentToSnackBarService.message = response.message || 'Sub Task Deletion Failed';
                this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
              }
              this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
            },
            error: (err: any) => {
              this.dataTobeSentToSnackBarService.message = err.message || 'Sub Task Deletion Failed';
              this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
              this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
            },
          });
        }
      },
    });
  }
  getsubTaskDetailsAll(pagIdx = 1) {
    const profileId = localStorage.getItem('profileId');
    const pagObj = {
      pageNumber: pagIdx,
      pageSize: this.itemsPerPage,
      sortField: this.sortField,
      sortOrder: this.sortOrder,
      keyword: this.field || 'empty', // search keyword
      ticketId: this.ticketid
    };

    return this.projectServ.getSubTaskAll(pagObj)
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (response: any) => {
          this.totalItems = response.data.subtasks.totalElements || 0;
          this.dataSource.data = response.data.subtasks.content.map((x: any, i: number) => ({
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

  getFullNameTooltip(users: any[]): string {
    return users?.map(u => ` ${u.fullname}`).join('\n') || '';
  }
  editingStatusId: number | null = null;

  onStatusChange(subtaskId: number, status: string): void {
    this.editingStatusId = null;

    const updatedby = localStorage.getItem('profileId'); // Fetch from localStorage

    if (!updatedby) {
      const snackBarData: ISnackBarData = {
        message: 'User not authorized.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        direction: 'above',
        panelClass: ['custom-snack-failure'],
      };
      this.snackBarServ.openSnackBarFromComponent(snackBarData);
      return;
    }



    this.projectServ.updatesubtaskstatus(subtaskId, status, updatedby).subscribe({
      next: (res: any) => {
        const isSuccess = res.status === 'success';

        const snackBarData: ISnackBarData = {
          message: res.message || (isSuccess ? 'Status updated successfully!' : 'Update failed.'),
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure'],
        };

        this.snackBarServ.openSnackBarFromComponent(snackBarData);
    this.getsubTaskDetailsAll()
       
      },
      error: (err: any) => {
        const snackBarData: ISnackBarData = {
          message: err?.error?.message || 'Something went wrong. Please try again.',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        };
        this.snackBarServ.openSnackBarFromComponent(snackBarData);
      }
    });
  }


  searchTimeout: any;

  onSearchChange() {
    clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => {
      this.currentPageIndex = 0; // Reset to first page when searching
      this.getsubTaskDetailsAll(1);
    }, 300); // Delay in ms
  }

  handlePageEvent(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.itemsPerPage = event.pageSize;
    this.currentPageIndex = event.pageIndex;
    this.getsubTaskDetailsAll(this.currentPageIndex + 1); // 1-based index for API
  }

  sortData(event: Sort) {
    this.sortField = event.active;
    this.sortOrder = event.direction || 'asc';
    this.getsubTaskDetailsAll(this.currentPageIndex + 1);
  }
  @ViewChild('descriptionDialog') descriptionDialog!: TemplateRef<any>;

  showDescription(row: any): void {
    const description = row.subTaskDescription || 'No description available';

    this.dialog.open(this.descriptionDialog, {
      data: { description },
      width: '600px'
    });
  }
  @ViewChild('commentsDialog') commentsDialog!: TemplateRef<any>;

  selectedTaskId!: any;
  selectedTicketId!: number;
  commentText: string = '';
  selectedStatus: any
  commentsList: any[] = [];

  showComments(row: any): void {
    this.selectedTaskId = row.subTaskId;
    this.selectedTicketId = row.ticketid;
    this.selectedStatus = row.status; // store status here

    this.getcommentTask();
    this.dialog.open(this.commentsDialog);
  }

  submitComment(): void {
    const commentTextarea = document.querySelector('textarea[name="comment"]') as HTMLTextAreaElement;

    if (!this.commentText || this.commentText.trim() === '') {
      // mark the field as touched to trigger mat-error
      commentTextarea.dispatchEvent(new Event('blur'));
      return;
    }

    const userId = localStorage.getItem('profileId');
    const payload = {
      subTaskId: this.selectedTaskId,
      ticketid: this.selectedTicketId,
      updatedby: Number(userId),
      comments: this.commentText.trim(),
      status: this.selectedStatus,

    };

    this.projectServ.commentAddSubtask(payload).subscribe({
      next: () => {
        this.snackBarServ.openSnackBarFromComponent({
          message: 'Comment added successfully!',
          duration: 2500,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-success']
        });
        this.commentText = '';
        this.getcommentTask();
      },
      error: (err: any) => {
        this.snackBarServ.openSnackBarFromComponent({
          message: err?.error?.message || 'Failed to add comment.',
          duration: 2500,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure']
        });
      }
    });
  }

  getcommentTask(): void {
    this.projectServ.getCommentsubTask(this.selectedTaskId).subscribe({
      next: (res: any) => {
        console.log(res.data, 'Fetched Comments');

        this.commentsList = res.data.map((x: any) => ({
          fullname: x.fullname,
          createddate: x.createddate,
          description: x.description

        }));
        console.log(this.commentsList, 'fullname')

      },

      error: (err: any) => {
        console.error('Error fetching comments:', err);
        this.commentsList = [];
      }
    });
  }


  closeCommentsDialog(): void {
    this.dialog.closeAll();
    this.commentText = '';
    this.commentsList = [];
  }
  
 openTemplateDialog(templateRef: TemplateRef<any>, row?: any) {
  this.selectedFilesReport = []; // 🔁 Clear previously selected files

  if (row?.subTaskId) {
    this.selectedTaskId = row.subTaskId;
    this.getSubuploadfiles(); // Fetch latest uploaded files
  }

  this.dialog.open(templateRef, {
    width: '500px'
  });
}



  selectedFilesdata: File[] = [];
  selectedFilesReport: File[] = [];
existingFiles: any[] = []; // from API or database

onFileSelectedReport(event: Event): void {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files.length > 0) {
    const fileArray = Array.from(input.files);
    this.selectedFilesReport = [...this.selectedFilesReport, ...fileArray];
  }
}

removeSelectedFile(index: number): void {
  this.selectedFilesdata.splice(index, 1);
}

submitFiles() {
  console.log(this.selectedTaskId,'selectedTaskIdselectedTaskId');
  
  if (!this.selectedFilesReport || this.selectedFilesReport.length === 0 || !this.selectedTaskId) {
    this.snackBarServ.openSnackBarFromComponent({
      message: 'No files selected or task ID missing.',
      duration: 2500,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-failure']
    });
    return;
  }

  const formData = new FormData();

  for (let file of this.selectedFilesReport) {
    formData.append('files', file);
  }

  formData.append('subTaskId', this.selectedTaskId);

  this.projectServ.saveuploadfile(formData).subscribe({
    next: () => {
      this.snackBarServ.openSnackBarFromComponent({
        message: 'Files uploaded successfully.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-success']
      });

      this.selectedFilesReport = [];
      this.getsubTaskDetailsAll(); // ✅ call your method here

      // 🔁 Refresh uploaded files list
      this.getSubuploadfiles();

      // ❌ Close the dialog
      this.dialog.closeAll(); // closes all open dialogs
    },
    error: () => {
      this.snackBarServ.openSnackBarFromComponent({
        message: 'File upload failed.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure']
      });
    }
  });
}

existingFilesReport: any[] = [];

getSubuploadfiles() {
  console.log(this.selectedTaskId, 'selectedtaskid');

  this.projectServ.getSubtaskuploadfile(this.selectedTaskId).subscribe((res: any) => {
    console.log(res, 'upload files response');

    if (Array.isArray(res.data)) {
      this.existingFilesReport = res.data;
    } else {
      this.existingFilesReport = [res.data]; // in case it's a single object
    }
  });
}
DeleteUploadfile(fileId: number) {
  this.projectServ.deleteuploadfile(fileId).subscribe({
    next: (res: any) => {
      const isSuccess = res?.status === 'success';
      const message = res?.message || (isSuccess ? 'File deleted successfully.' : 'Failed to delete file.');

      // Show snackbar
      this.snackBarServ.openSnackBarFromComponent({
        message: message,
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure']
      });

      if (isSuccess) {
        // Refresh file list after deletion
        this.getSubuploadfiles();
      }
    },
    error: (err) => {
      console.error('Error deleting file:', err);
      this.snackBarServ.openSnackBarFromComponent({
        message: 'Something went wrong while deleting the file.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure']
      });
    }
  });
}
}
