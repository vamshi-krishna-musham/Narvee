import { inject, Injectable } from '@angular/core';
import { ApiserviceService } from '../PathService/apiservice.service';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TaskmanagementService {
  private apiServ = inject(ApiserviceService);

  constructor(private http:HttpClient) { }
  managementregister(payload :any){
    return this.apiServ.post("auth/tms/userRegistration",payload)
    }
    managementlogin(payload:any){
      return this.apiServ.post("auth/tms/authantication-tms",payload)
    }

  EmailVerify(payload:any){
return this.apiServ.post("auth/tms/forgotPassword",payload)
  }

  validateotp(id:any,otp:any){
    return this.apiServ.get(`auth/tms/validate/${id}/${otp}`)
  }
  SentPassword(payload:any){
    return this.apiServ.post('auth/tms/change_password',payload)
  }
  validatepassword(payload:any){
    return this.apiServ.post('auth/tms/verify-old-password',payload)
  }
  verifyOtp(payload:any){
    return this.apiServ.post('auth/tms/login',payload)
  }
  updateTeamMember(payload:any){
    return this.apiServ.post('auth/tms/UpdateTmsUser',payload)
  }
  TeamMemberGetId(userid:any){
 return this.apiServ.get(`auth/tms/findByid/${userid}`)
 }
 uploadphoto(payload:any){
  return this.apiServ.post(`auth/tms/uploadPic`,payload)
 }
 removeuserphoto(id:any){
  return this.apiServ.delete(`auth/tms/deleleProfilepic/${id}`)
 }
 NotificationSave(payload:any){
  return this.apiServ.post(`task/EmailConfiguration/saveEmailNotifications`,payload)
 }
 GetNotification(id:any){
  return this.apiServ.get(`task/EmailConfiguration/getAllEmailNotifications/${id}`)
 }
 NotificationUpdate(payload:any){
  return this.apiServ.put(`task/EmailConfiguration/updateEmailNotification`,payload)

 }
}