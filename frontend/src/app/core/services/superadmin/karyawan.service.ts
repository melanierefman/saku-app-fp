import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseApiService } from '../base-api.service';
import {
  Karyawan,
  KaryawanQueryParams,
  KaryawanCreateRequest,
  KaryawanUpdateRequest,
  PageResponse,
} from '../../models/superadmin/karyawan.model';

@Injectable({
  providedIn: 'root',
})
export class KaryawanService extends BaseApiService<
  Karyawan,
  KaryawanCreateRequest,
  KaryawanUpdateRequest
> {
  protected endpoint = 'karyawan';

  // Mengambil daftar karyawan terpaginasi beserta filter
  getKaryawanPage(params?: KaryawanQueryParams): Observable<PageResponse<Karyawan>> {
    return this.getPaginated(params);
  }
}
