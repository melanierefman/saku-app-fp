import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { BaseApiService } from '../base-api.service';
import { Permission, PermissionRequest } from '../../models/superadmin/permission.model';
import { ApiResponse } from '../../models/auth/auth.models';

@Injectable({
  providedIn: 'root',
})
export class PermissionService extends BaseApiService<
  Permission,
  PermissionRequest,
  PermissionRequest
> {
  protected endpoint = 'master/permission';

  // Find All by Menu Id
  findAllByMenuId(menuId?: string): Observable<Permission[]> {
    let params = new HttpParams();
    if (menuId) {
      params = params.set('menuId', menuId);
    }

    return this.http
      .get<ApiResponse<Permission[]> | Permission[]>(this.fullUrl, { params })
      .pipe(
        map((res) => {
          if (res && typeof res === 'object' && 'data' in res) {
            return (res as ApiResponse<Permission[]>).data;
          }
          return (res as Permission[]) || [];
        })
      );
  }
}
