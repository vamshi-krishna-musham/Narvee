import { inject, Injectable } from '@angular/core';
import { ApiserviceService } from './apiservice.service';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private apiServ = inject(ApiserviceService);
  constructor() { }

  getAllTasksOfProject(entity: any) {
    return this.apiServ.post("task/findByProjectId", entity);
  }

  getUsersByDepartment(department: string) {
    return this.apiServ.get(`task/getUsers/${department}`);
  }

  getUsersByProject(projectId: any) {
    return this.apiServ.get(`task/getProjectUsers/${projectId}`)
  }

  createTask(entity: any) {
    return this.apiServ.post("task/createTask", entity);
  }

  getTaskById(taskid: number | string) {
    return this.apiServ.get("task/getbyTaskId/" + taskid);
  }

  public Taskupdate(entity: any) {
    return this.apiServ.post("task/update", entity);
  }

  trackByUser(id: number) {
    return this.apiServ.get("task/trackByUser/" + id);
  }

  getTaskByTicketId(ticketid: string) {
    return this.apiServ.get("task/getByTicketId/" + ticketid);
  }

  public popup(taskid: any) {
    return this.apiServ.get("task/taskAssinInfo/" + taskid);
  }

  task_report(value: any) {
    return this.apiServ.post("task/getTaskReports", value);
  }

  deleteTask(id: any) {
    return this.apiServ.delete(`task/delete/${id}`);
  }

  updateTaskStatus(taskid: any, status: any, userid: any) {
    return this.apiServ.get(`task/update/${taskid}/${status}/${userid}`);
  }

  addORUpdateTask(entity: any, action: 'edit-task' | 'add-task') {
    return action === 'edit-task' ? this.Taskupdate(entity) : this.createTask(entity);
  }

  taskComments(taskid: any) {
    return this.apiServ.get(`task/trackByTask/${taskid}`);
  }

  addComments(data: any) {
    return this.apiServ.post(`task/updateTask`, data);
  }
  getTaskCountDeafult(userId: any) {
    return this.apiServ.get(`task/dashboard/getTaskCountByAdminId/${userId}`)
  }
  getProjects(userId: any) {
    return this.apiServ.get(`task/dashboard/dropDown/${userId}`)
  }
  getTaskCountProject(pid: any, userid: any) {
    return this.apiServ.get(`task/dashboard/getTaskCountByPidAndUserId/${pid}/${userid}`)

  }
getCountTimeInterval(pid?: any, userId?: any, timeInterval?: string) {
  let params = new HttpParams();

  if (pid) params = params.set('pid', pid);
  if (userId) params = params.set('userId', userId);
  if (timeInterval) params = params.set('time', timeInterval);

  return this.apiServ.get(`task/dashboard/getTaskCountByPidAndUserIdAndTime`, { params });
}
getTaskPriorityDefault(userid:any){
  return this.apiServ.get(`task/dashboard/getPriorityCountByAdminId/${userid}`)
}

getTaskPriorityProject(pid:any,userid:any){
return this.apiServ.get(`task/dashboard/getPriorityCountByPidAndUserId/${pid}/${userid}`)
}
getTaskprojectCountInterval(pid?: any, userId?: any, timeInterval?: string) {
  let params = new HttpParams();

  if (pid) params = params.set('pid', pid);
  if (userId) params = params.set('userId', userId);
  if (timeInterval) params = params.set('time', timeInterval);

  return this.apiServ.get(`task/dashboard/getPriorityCountByPidAndUserIdAndTime`, { params });
}

GetBarTaskCount(payload:any){
return this.apiServ.post(`task/dashboard/get-completed-count`,payload)
}
DropdownInterval(timeInterval:any){
  return this.apiServ.get(`task/dashboard/daily-dropDown/${timeInterval}`)
}
getTeamPerformance(projectId?: any, adminId?: any, timeInterval?: string) {
  let params = new HttpParams();

  if (projectId) params = params.set('projectId', projectId);
  if (adminId) params = params.set('adminId', adminId);
  if (timeInterval) params = params.set('timeInterval', timeInterval);

  return this.apiServ.get(`task/dashboard/getUserTrackerByAdmin`, { params });
}
}
