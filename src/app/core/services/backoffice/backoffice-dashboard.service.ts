import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../models/auth/auth.models';
import { BackofficeDashboardStats } from '../../models/backoffice/backoffice-dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class BackofficeDashboardService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/backoffice/dashboard`;

  getDashboardStats(): Observable<ApiResponse<BackofficeDashboardStats>> {
    return this.http.get<ApiResponse<BackofficeDashboardStats>>(`${this.baseUrl}/stats`).pipe(
      catchError((err) => {
        // Fallback to alternative endpoint path /api/bo/dashboard/stats if 404
        if (err.status === 404) {
          return this.http.get<ApiResponse<BackofficeDashboardStats>>(
            `${environment.apiUrl}/bo/dashboard/stats`
          );
        }
        return throwError(() => err);
      })
    );
  }
}
