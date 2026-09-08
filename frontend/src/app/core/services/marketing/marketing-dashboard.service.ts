import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../models/auth/auth.models';
import { MarketingDashboardStats } from '../../models/marketing/marketing-dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class MarketingDashboardService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/marketing/dashboard`;

  getDashboardStats(): Observable<ApiResponse<MarketingDashboardStats>> {
    return this.http.get<ApiResponse<MarketingDashboardStats>>(`${this.baseUrl}/stats`);
  }
}
