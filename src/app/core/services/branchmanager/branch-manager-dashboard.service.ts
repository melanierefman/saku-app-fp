import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../models/auth/auth.models';
import { BranchManagerDashboardStats } from '../../models/branchmanager/branch-manager-dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class BranchManagerDashboardService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/branch-manager/dashboard`;

  getDashboardStats(): Observable<ApiResponse<BranchManagerDashboardStats>> {
    return this.http.get<ApiResponse<BranchManagerDashboardStats>>(`${this.baseUrl}/stats`).pipe(
      catchError((err) => {
        // Fallback to alternative endpoint path without hyphen if 404
        if (err.status === 404) {
          return this.http.get<ApiResponse<BranchManagerDashboardStats>>(
            `${environment.apiUrl}/branchmanager/dashboard/stats`
          );
        }
        return throwError(() => err);
      })
    );
  }
}
