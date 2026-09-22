import { Injectable } from '@angular/core';
import { Observable, map, catchError } from 'rxjs';
import { BaseApiService } from '../base-api.service';
import {
  Role,
  RoleRequest,
  RoleDetailResponse,
  AssignPermissionsRequest,
} from '../../models/superadmin/role.model';
import { ApiResponse } from '../../models/auth/auth.models';

@Injectable({
  providedIn: 'root',
})
export class RoleService extends BaseApiService<Role, RoleRequest, RoleRequest> {
  protected endpoint = 'master/role';

  // Menugaskan daftar permissions ke peran (role) tertentu
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
}
