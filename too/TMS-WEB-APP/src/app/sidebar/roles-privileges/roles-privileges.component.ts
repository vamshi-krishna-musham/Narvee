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
import { AddTaskviewComponent } from '../add-taskview/add-taskview.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AddSubtaskviewComponent } from '../add-subtaskview/add-subtaskview.component';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { IConfirmDialogData } from '../sidenav/projects/confirm-dialog-data';
import { DialogService } from '../../PathService/dialog.service';
import { CommonDeleteComponent } from '../common-delete/common-delete.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
@Component({
  selector: 'app-roles-privileges',
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
    
    MatCheckboxModule     // Material checkbox
    ],
  templateUrl: './roles-privileges.component.html',
  styleUrl: './roles-privileges.component.scss'
})
export class RolesPrivilegesComponent {
  displayedColumns: string[] = ['role'];
  permissionList = [
    { key: 'fullAccess', label: 'Full System Access' },
    { key: 'manageUsers', label: 'Manage Users' },
    { key: 'manageProjects', label: 'Create/Edit/Delete Projects' },
    { key: 'manageTasks', label: 'Create/Edit/Delete Tasks' },
    { key: 'assignTasks', label: 'Assign Tasks' },
    { key: 'updateStatus', label: 'Update Status' },
    { key: 'uploadReports', label: 'Upload Reports' },
    { key: 'accessSettings', label: 'Access Settings' },
    { key: 'viewReports', label: 'View Reports' }
  ];
  
  // Build displayedColumns dynamically
  ngOnInit() {
    this.displayedColumns.push(...this.permissionList.map(p => p.key));
  }
  
  roleData = [
    {
      role: 'Project Manager',
      permissions: {
        fullAccess: true,
        manageUsers: true,
        manageProjects: true,
        manageTasks: true,
        assignTasks: true,
        updateStatus: true,
        uploadReports: true,
        accessSettings: true,
        viewReports: true
      }
    },
    {
      role: 'Admin',
      permissions: {
        fullAccess: true,
        manageUsers: true,
        manageProjects: true,
        manageTasks: true,
        assignTasks: true,
        updateStatus: true,
        uploadReports: true,
        accessSettings: true,
        viewReports: true
      }
    },
    {
      role: 'Team Lead',
      permissions: {
        fullAccess: false,
        manageUsers: false,
        manageProjects: true,
        manageTasks: true,
        assignTasks: true,
        updateStatus: true,
        uploadReports: true,
        accessSettings: false,
        viewReports: true
      }
      
    },
    {
      role: 'Team Member',
      permissions: {
        fullAccess: false,
        manageUsers: false,
        manageProjects: true,
        manageTasks: true,
        assignTasks: true,
        updateStatus: true,
        uploadReports: true,
        accessSettings: false,
        viewReports: true
      }
      
    } ,
    {
      role: 'Client/External User',
      permissions: {
        fullAccess: false,
        manageUsers: false,
        manageProjects: true,
        manageTasks: true,
        assignTasks: true,
        updateStatus: true,
        uploadReports: true,
        accessSettings: false,
        viewReports: true
      }
      
    } ,
    {
      role: 'Observer/Auditor',
      permissions: {
        fullAccess: false,
        manageUsers: false,
        manageProjects: true,
        manageTasks: true,
        assignTasks: true,
        updateStatus: true,
        uploadReports: true,
        accessSettings: false,
        viewReports: true
      }
      
    }
    // Add more roles as needed
  ];
  
  addRole() {
    console.log('Add Role clicked');
  }
  
  onPermissionChange(role: any, permissionKey: string) {
    console.log(`Permission ${permissionKey} for ${role.role} changed to ${role.permissions[permissionKey]}`);
  }
  
  
}
