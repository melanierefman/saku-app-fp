import { Injectable } from '@angular/core';
import { BaseApiService } from '../base-api.service';
import { Permission, PermissionRequest } from '../../models/superadmin/permission.model';

@Injectable({
  providedIn: 'root',
})
export class PermissionService extends BaseApiService<
  Permission,
  PermissionRequest,
  PermissionRequest
> {
  protected endpoint = 'master/permission';
}
