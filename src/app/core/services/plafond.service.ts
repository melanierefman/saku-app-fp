import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { Plafond, PlafondRequest } from '../models/plafond.model';

@Injectable({
  providedIn: 'root',
})
export class PlafondService extends BaseApiService<Plafond, PlafondRequest, PlafondRequest> {
  protected endpoint = 'plafond';
}
