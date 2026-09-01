import { Injectable } from '@angular/core';
import { Observable, map, catchError, of } from 'rxjs';
import { BaseApiService } from './base-api.service';
import { Cabang, CabangRequest } from '../models/cabang.model';
import { ApiResponse } from '../models/auth.models';

@Injectable({
  providedIn: 'root',
})
export class CabangService extends BaseApiService<Cabang, CabangRequest, CabangRequest> {
  protected endpoint = 'cabang';

  // GET /api/cabang/active
  findAllActive(): Observable<Cabang[]> {
    return this.http
      .get<ApiResponse<Cabang[]> | Cabang[]>(`${this.fullUrl}/active`)
      .pipe(
        map((res: any) => {
          if (res && typeof res === 'object' && 'data' in res) {
            return Array.isArray(res.data) ? res.data : [];
          }
          return Array.isArray(res) ? res : [];
        }),
        catchError(() => of([]))
      );
  }
}
