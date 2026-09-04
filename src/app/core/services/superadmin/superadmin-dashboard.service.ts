import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../models/auth/auth.models';
import { SuperadminDashboardStats } from '../../models/superadmin/superadmin-dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class SuperadminDashboardService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/master/dashboard`;

  getDashboardStats(): Observable<ApiResponse<SuperadminDashboardStats>> {
    return this.http.get<ApiResponse<SuperadminDashboardStats>>(`${this.baseUrl}/stats`);
  }
}
