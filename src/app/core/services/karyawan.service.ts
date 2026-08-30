import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseApiService } from './base-api.service';
import {
  Karyawan,
  KaryawanQueryParams,
  KaryawanCreateRequest,
  KaryawanUpdateRequest,
  PageResponse,
} from '../models/karyawan.model';

@Injectable({
  providedIn: 'root',
})
export class KaryawanService extends BaseApiService<
  Karyawan,
  KaryawanCreateRequest,
  KaryawanUpdateRequest
> {
  protected endpoint = 'karyawan';

  // GET Paginated
  getKaryawanPage(params?: KaryawanQueryParams): Observable<PageResponse<Karyawan>> {
    return this.getPaginated(params);
  }

  // GET All Unpaginated
  getAllKaryawan(): Observable<Karyawan[]> {
    return this.getAllUnpaginated();
  }
}
