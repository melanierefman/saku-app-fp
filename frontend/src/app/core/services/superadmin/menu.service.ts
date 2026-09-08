import { Injectable } from '@angular/core';
import { BaseApiService } from '../base-api.service';
import { Menu, MenuRequest } from '../../models/superadmin/menu.model';

@Injectable({
  providedIn: 'root',
})
export class MenuService extends BaseApiService<Menu, MenuRequest, MenuRequest> {
  protected endpoint = 'master/menu';
}
