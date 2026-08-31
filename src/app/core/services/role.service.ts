import { Injectable } from '@angular/core';
import { Observable, map, catchError, of, timeout } from 'rxjs';
import { BaseApiService } from './base-api.service';
import {
  Role,
  RoleRequest,
  RoleDetailResponse,
  AssignPermissionsRequest,
  Permission,
} from '../models/role.model';
import { ApiResponse } from '../models/auth.models';

@Injectable({
  providedIn: 'root',
})
export class RoleService extends BaseApiService<Role, RoleRequest, RoleRequest> {
  protected endpoint = 'master/role';

  // Assign Permissions to Role (PUT /api/master/role/{id}/permissions)
  assignPermissions(
    id: string,
    permissionIds: string[]
  ): Observable<RoleDetailResponse> {
    const payload: AssignPermissionsRequest = { permissionIds };
    return this.http
      .put<ApiResponse<RoleDetailResponse> | RoleDetailResponse>(
        `${this.fullUrl}/${id}/permissions`,
        payload
      )
      .pipe(
        map((res) => {
          if (res && typeof res === 'object' && 'data' in res) {
            return (res as ApiResponse<RoleDetailResponse>).data;
          }
          return res as RoleDetailResponse;
        }),
        catchError(() => {
          const altUrl = `${this.baseUrl.replace(/\/+$/, '')}/role/${id}/permissions`;
          return this.http
            .put<ApiResponse<RoleDetailResponse> | RoleDetailResponse>(altUrl, payload)
            .pipe(
              map((res) => {
                if (res && typeof res === 'object' && 'data' in res) {
                  return (res as ApiResponse<RoleDetailResponse>).data;
                }
                return res as RoleDetailResponse;
              })
            );
        })
      );
  }

  // Get Available Permissions Catalog (Full API)
  getAvailablePermissions(): Observable<Permission[]> {
    const permUrl = `${this.baseUrl.replace(/\/+$/, '')}/master/permission`;
    const altPermUrl = `${this.baseUrl.replace(/\/+$/, '')}/permission`;

    return this.http.get<ApiResponse<Permission[]> | Permission[]>(permUrl).pipe(
      map((res: any) => {
        const data = res?.data || res;
        return Array.isArray(data) ? data : [];
      }),
      catchError(() => {
        return this.http.get<ApiResponse<Permission[]> | Permission[]>(altPermUrl).pipe(
          map((res: any) => {
            const data = res?.data || res;
            return Array.isArray(data) ? data : [];
          }),
          catchError(() => of([]))
        );
      })
    );
  }
}
