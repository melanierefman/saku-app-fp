import { Injectable } from '@angular/core';
import { BaseApiService } from '../base-api.service';
import { Cabang, CabangRequest } from '../../models/superadmin/cabang.model';

@Injectable({
  providedIn: 'root',
})
export class CabangService extends BaseApiService<Cabang, CabangRequest, CabangRequest> {
  protected endpoint = 'cabang';
}
