import { inject, Injectable } from '@angular/core';
import { ApiserviceService } from '../PathService/apiservice.service';
import { Observable, retry } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private apiServ = inject(ApiserviceService);

  constructor() { }
  createRole(payload:any){
    return this.apiServ.post('auth/tmsRoles/save',payload)

  }
  getRole(payload:any){
    return this.apiServ.post(`auth/tmsRoles/all`,payload)

  }
  getresourcerole(adminId:any){
        return this.apiServ.get(`auth/tmsRoles/all/${adminId}`)

  }
  deleteRole(id:any){
    return this.apiServ.delete(`auth/tmsRoles/delete/${id}`)

  }
updateRole(payload:any){
  return this.apiServ.put('auth/tmsRoles/updateRole',payload)

}
saveprivilege(payload:any){
  return this.apiServ.post('auth/tmsPrivilege/savePrevileges',payload)

}

getPrivilegesbyID(roleid:any){
  return this.apiServ.get(`auth/tmsPrivilege/getPrivilegesById/${roleid}`)

}
getPrivileges(){
  return this.apiServ.get(`auth/tmsPrivilege/getPrivileges`)

}

SaveRolePrivilege(payload: { roleId: string; privilegeIds: any[] }){
  return this.apiServ.post('auth/tmsPrivilege/addprevtorole',payload)
}
}
