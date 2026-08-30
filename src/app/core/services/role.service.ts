import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { Role } from '../models/role.model';

@Injectable({
  providedIn: 'root',
})
export class RoleService extends BaseApiService<Role> {
  protected endpoint = 'master/role';
}
