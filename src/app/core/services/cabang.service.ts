import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { Cabang } from '../models/cabang.model';

@Injectable({
  providedIn: 'root',
})
export class CabangService extends BaseApiService<Cabang> {
  protected endpoint = 'cabang';
}
