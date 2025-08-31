import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterManagmentComponent } from './Components/register-managment/register-managment.component';
import { SidenavComponent } from './sidebar/sidenav/sidenav.component';
import { ProjectsComponent } from './sidebar/sidenav/projects/projects.component';
import { TeamMemberComponent } from './sidebar/team-member/team-member.component';
import { ChangepasswordComponent } from './sidebar/changepassword/changepassword.component';
import { AddTaskComponent } from './sidebar/add-task/add-task.component';
import { AddSubtaskComponent } from './sidebar/add-subtask/add-subtask.component';
import { RolesPrivilegesComponent } from './sidebar/roles-privileges/roles-privileges.component';
import { RolesAddComponent } from './sidebar/roles-add/roles-add.component';
import { AddNewRoleComponent } from './sidebar/add-new-role/add-new-role.component';
import { DashboardComponent } from './sidebar/dashboard/dashboard.component';
import { ViewPrivilegeComponent } from './sidebar/view-privilege/view-privilege.component';
import { ViewProfileComponent } from './sidebar/view-profile/view-profile.component';
import { NotificationsComponent } from './sidebar/notifications/notifications.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  {path:'',redirectTo:'/register-login',pathMatch:'full'},
  {path:'register-login',component:RegisterManagmentComponent},
  {
    path: '',
    component: SidenavComponent,  canActivate: [AuthGuard],
    children: [
      { path: 'projects', component: ProjectsComponent,  canActivate: [AuthGuard], },
      {path:'teammember',component:TeamMemberComponent ,  canActivate: [AuthGuard],},
      {path:'changepassword',component:ChangepasswordComponent ,  canActivate: [AuthGuard],},
      {path:'addtask',component:AddTaskComponent ,  canActivate: [AuthGuard],},
      {path:'add-subtask',component:AddSubtaskComponent ,  canActivate: [AuthGuard],},
      {path:'roles-privileges',component:RolesPrivilegesComponent ,  canActivate: [AuthGuard],},
      {path:'roles-add',component:RolesAddComponent ,  canActivate: [AuthGuard],},
      {path:'add-newrole',component:AddNewRoleComponent ,  canActivate: [AuthGuard],},
      {path:'Dashboard',component:DashboardComponent ,  canActivate: [AuthGuard],},
      {path:'view-privilege',component:ViewPrivilegeComponent ,  canActivate: [AuthGuard],},
      {path:'view-profile',component:ViewProfileComponent ,  canActivate: [AuthGuard],},
      {path:'notification',component:NotificationsComponent ,  canActivate: [AuthGuard],},
       {
    path: '**',
    redirectTo: 'register-login'
  }
      // other routes
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
