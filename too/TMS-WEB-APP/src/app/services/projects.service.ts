import { inject, Injectable } from '@angular/core';
import { ApiserviceService } from '../PathService/apiservice.service';
import { Observable, retry } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProjectsService {

  private apiServ = inject(ApiserviceService);
  private pid!: string | number ;

  constructor() { }

  saveProject(entity: any) {
    return this.apiServ.post("task/project/save", entity);
  }

  updateProject(entity: any) {
    return this.apiServ.put("task/project/update", entity);
  }

  getAllProjectsWithPaginationAndSorting(entity: any) {
    return this.apiServ.post("task/project/findAllProjects", entity);
  }

  getProjectById(id: any) {
    return this.apiServ.get(`task/project/getbyProjectId/${id}`);
  }

  addORUpdateProject(entity: any, action: 'edit-project' | 'add-project'){
    return action === 'edit-project' ? this.updateProject(entity): this.saveProject(entity);
  }

  

  setPid(pid: string | number): void {
    this.pid = pid;
  }

  getPid(): string | number {
    return this.pid;
  }
  getuserdetails(payload:any){
  return this.apiServ.post(`auth/tms/getAllUsers`,payload)
  }
  getteammembersdropdown(profileId:any){
return this.apiServ.get(`auth/tms/getUsersDropDown/${profileId}`)
  }

  teamMemberSave(payload:any){
    return this.apiServ.post('task/project/save_tms',payload)
  }
  getprojectdetails(payload:any){
    return this.apiServ.post('task/project/findAllProjects-tms',payload)
  }
  teamMemberUpdate(payload:any){
    return this.apiServ.post('task/project/update-tms',payload)
  }
  deleteProject(id: any) {
    return this.apiServ.delete(`task/project/delete/${id}`);
  }
  getProjectDetailsbyId(pid:any){
    return this.apiServ.get(`task/project/getbyProjectIdTms/${pid}`)
  }
  updateProjectDetails(payload:any){
    return this.apiServ.put('task/project/update-tms',payload)
  }
  CreateTask(payload:any){
    return this.apiServ.post('task/createTmsTask',payload)
  }
  getAssignedDropdown(projectId:any){
    return this.apiServ.get(`task/getProjectUsers/${projectId}`)
  }
  getprojectTasks(payload:any){
    return this.apiServ.post(`task/findByProjectId-tms`,payload)
  }
  updatestatustasks(payload:any){
    return this.apiServ.post(`task/updateTask-tms`,payload)
  }
  getTaskByIdDetails(taskid:any){
    return this.apiServ.get(`task/getbyTaskId-tms/${taskid}`)
  }
  updateTaskdetails(payload:any){
    return this.apiServ.post('task/updateTmsTask',payload)
  }
  DeleteTaskDetails(taskId:any){
return this.apiServ.delete(`task/delete-tms/${taskId}`)
  }
  CreateSubTask(payload:any){
    return this.apiServ.post('task/subTask/saveSubTask-tms',payload)
  }
  getSubTaskAll(payload:any){
    return this.apiServ.post('task/subTask/getBySubTaskTicketId',payload)
  }
  updatesubtaskstatus(subtaskId: number, status: string, updatedby: any) {
    return this.apiServ.get(`task/subTask/updateSubTaskStatus-tms/${subtaskId}/${status}/${updatedby}`);
  }
  updatetaskstatus(taskId: number, status: string, updatedby: any) {
    return this.apiServ.get(`task/updateTmsTaskStatus/${taskId}/${status}/${updatedby}`);
  }
  deletesubTask(subtaskId:any){
    return this.apiServ.delete(`task/subTask/deleteSubTask-tms/${subtaskId}`)

  }
  getSubTaskId(subtaskId:any){
    return this.apiServ.get(`task/subTask/getBySubTaskId-tms/${subtaskId}`)
  }
  updatesubTaskdetails(payload:any){
    return this.apiServ.put('task/subTask/updateSubTask-tms',payload)
  }

  
  downloadfile(id: any): Observable<Blob> {
    return this.apiServ.get(`task/project/download-file/${id}`, {
      responseType: 'blob'
    });
  }
  deletefile(id:any){
    return this.apiServ.delete(`task/deleteFile-tms/${id}`)
  }
  commentAddTask(payload:any){
    return this.apiServ.post('task/updateTask-tms',payload)
  }
  
  getcommentTask(id:any){
    return this.apiServ.get(`task/trackByTask-tms/${id}`)
  }
  commentAddSubtask(payload:any){
     return this.apiServ.post('task/subTask/updateTmsSubTaskTrack',payload)
  }
  getCommentsubTask(id:any){
    return this.apiServ.get(`task/subTask/trackBySubTask-tms/${id}`)


  }
DeleteTeamMember(id:any){
      return this.apiServ.delete(`auth/tms/DeleteTeamMember/${id}`)

}

saveuploadfile(payload:any){
  return this.apiServ.post('task/fileUpload/save',payload)

}
getuploadfile(id: any) {
  return this.apiServ.get(`task/fileUpload/get-files?taskId=${id}`);
}

deleteuploadfile(fileid:any){
  return this.apiServ.delete(`task/fileUpload/delete-files/${fileid}`)
}
updateuploadfile(id: number, payload: any) {
  return this.apiServ.put(`task/fileUpload/UpdateFile/${id}`, payload);
}
getSubtaskuploadfile(id: any) {
  return this.apiServ.get(`task/fileUpload/get-files?subTaskId=${id}`);
}


}
