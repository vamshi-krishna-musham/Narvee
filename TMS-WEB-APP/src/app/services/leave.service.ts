import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

const API_BASE = 'http://localhost:2452';
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELED' | 'DENIED';

export interface LeaveRequest {
  id?: number;
  userId?: number;          // server sets from auth context
  userName?: string;        // optional (server can enrich for approvals view)
  leaveType: string;
  startDate: string;        // ISO date string (yyyy-MM-dd) from server
  endDate: string;          // ISO date string (yyyy-MM-dd)
  reason: string;
  status?: LeaveStatus;
  adminComment?: string;    // <-- NEW field added
  createdAt?: string;       // ISO datetime
  updatedAt?: string;       // ISO datetime
}

@Injectable({ providedIn: 'root' })
export class LeaveService {
  private base = API_BASE;

  constructor(private http: HttpClient) {}

  // ----- Member APIs -----
  apply(body: any) {
    return this.http.post(`${this.base}/task/leaves`, body);
  }

  listMine(id: number): Observable<LeaveRequest[]> {
    return this.http.get<any[]>(`${this.base}/task/leaves/user/${id}`).pipe(
      map(res => res.map(r => ({
        id: r.id,
        userId: r.userId,
        startDate: r.fromDate,
        endDate: r.toDate,
        createdAt: r.createdAt,
        leaveType: r.leaveCategory,
        reason: r.reason,
        status: r.status,
        adminComment: r.adminComment   // ✅ map backend field
      })))
    );
  }


  cancel(id: number) {
    return this.http.patch(`${this.base}/task/leaves/${id}/cancel`, {});
  }

  // ----- Admin / Super Admin APIs -----
  listPending(): Observable<LeaveRequest[]> {
    return this.http.get<any[]>(`${this.base}/task/leaves/pending`).pipe(
      map(res => res.map(r => ({
        id: r.id,
        userId: r.userId,
        userName: r.userName,
        startDate: r.fromDate,
        endDate: r.toDate,
        leaveType: r.leaveCategory,
        reason: r.reason,
        status: r.status,
        adminComment: r.adminComment   // ✅ include this too
      })))
    );
  }

  approve(id: number, adminComment?: string) {
    const body: any = { status: 'APPROVED' };
    if (adminComment) body.adminComment = adminComment;
    return this.http.patch(`${this.base}/task/leaves/${id}/approve`, body);
  }

  deny(id: number, adminComment?: string) {
    const body: any = { status: 'DENIED' };
    if (adminComment) body.adminComment = adminComment;
    return this.http.patch(`${this.base}/task/leaves/${id}/deny`, body);
  }
}
