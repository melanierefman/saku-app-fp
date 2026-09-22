import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../models/auth/auth.models';
import { PageResponse } from '../../models/superadmin/karyawan.model';
import {
  MarketingCustomerItemResponse,
  MarketingCustomerDetailResponse,
} from '../../models/marketing/marketing-customer.model';

@Injectable({
  providedIn: 'root',
})
export class MarketingCustomerService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/marketing/customers`;

  findAllPaginated(params?: {
    page?: number;
    size?: number;
    search?: string;
    tier?: string;
  }): Observable<PageResponse<MarketingCustomerItemResponse>> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', String(params.page));
      if (params.size !== undefined) httpParams = httpParams.set('size', String(params.size));
      if (params.search) httpParams = httpParams.set('search', params.search);
      if (params.tier) httpParams = httpParams.set('tier', params.tier);
    }

    return this.http
      .get<ApiResponse<PageResponse<MarketingCustomerItemResponse>> | any>(this.baseUrl, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => {
          if (res?.data?.content !== undefined) {
            return res.data;
          }
          if (res?.content !== undefined) {
            return res;
          }
          return {
            content: Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [],
            totalElements: res?.data?.totalElements || res?.totalElements || 0,
            totalPages: res?.data?.totalPages || res?.totalPages || 0,
            currentPage: params?.page || 0,
            pageSize: params?.size || 10,
          };
        }),
        catchError((err) => {
          console.warn('Failed to fetch marketing verified customers:', err);
          return of({
            content: [],
            totalElements: 0,
            totalPages: 0,
            currentPage: 0,
            pageSize: params?.size || 10,
          });
        })
      );
  }

  getDetail(customerId: string): Observable<MarketingCustomerDetailResponse | null> {
    return this.http
      .get<ApiResponse<MarketingCustomerDetailResponse>>(`${this.baseUrl}/${customerId}`)
      .pipe(
        map((res) => res?.data || null),
        catchError((err) => {
          console.error(`Failed to get customer detail ${customerId}:`, err);
          return of(null);
        })
      );
  }

  getFileUrl(path?: string | null): string {
    if (!path) return '';
    const baseHost = environment.apiUrl.replace(/\/api\/?$/, '');

    if (path.startsWith('http://') || path.startsWith('https://')) {
      return path
        .replace('/api/pinjaman/', '/uploads/pinjaman/')
        .replace('/api/ktp/', '/uploads/ktp/')
        .replace('/api/selfie/', '/uploads/selfie/');
    }

    let cleanPath = path.replace(/\\/g, '/');
    if (cleanPath.startsWith('/')) {
      cleanPath = cleanPath.substring(1);
    }
    if (!cleanPath.startsWith('uploads/')) {
      cleanPath = `uploads/${cleanPath}`;
    }
    return `${baseHost}/${cleanPath}`;
  }
}
