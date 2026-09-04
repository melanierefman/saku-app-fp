import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, timeout, map, catchError, of } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../models/auth/auth.models';
import { PageResponse } from '../../models/superadmin/karyawan.model';
import {
  MarketingPengajuanItemResponse,
  MarketingPengajuanDetailResponse,
} from '../../models/superadmin/monitoring-pengajuan.model';

@Injectable({
  providedIn: 'root',
})
export class MonitoringPengajuanService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/master/monitoring-pengajuan`;
  private readonly fallbackUrl = `${environment.apiUrl}/monitoring/pengajuan`;

  // Get Paginated Data All
  findAllPaginated(params?: {
    page?: number;
    size?: number;
    search?: string;
    status?: string;
    branchId?: string;
  }): Observable<PageResponse<MarketingPengajuanItemResponse>> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', String(params.page));
      if (params.size !== undefined) httpParams = httpParams.set('size', String(params.size));
      if (params.search) httpParams = httpParams.set('search', params.search);
      if (params.status) httpParams = httpParams.set('status', params.status);
      if (params.branchId) httpParams = httpParams.set('branchId', params.branchId);
    }

    return this.http
      .get<ApiResponse<PageResponse<MarketingPengajuanItemResponse>> | any>(this.baseUrl, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => this.parseResponse(res, params?.size || 10)),
        catchError((err) => {
          console.warn('Primary monitoring-pengajuan URL failed, trying fallback:', err);
          return this.http
            .get<any>(this.fallbackUrl, { params: httpParams })
            .pipe(
              map((res: any) => this.parseResponse(res, params?.size || 10)),
              catchError(() =>
                of({
                  content: [],
                  totalElements: 0,
                  totalPages: 0,
                  currentPage: 0,
                  pageSize: params?.size || 10,
                })
              )
            );
        })
      );
  }

  // Get Unpaginated Data All
  findAll(status?: string): Observable<MarketingPengajuanItemResponse[]> {
    let httpParams = new HttpParams();
    if (status) httpParams = httpParams.set('status', status);

    return this.http
      .get<ApiResponse<MarketingPengajuanItemResponse[]> | any>(`${this.baseUrl}/all`, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => this.normalizeList(res)),
        catchError(() => {
          return this.http
            .get<any>(`${this.fallbackUrl}/all`, { params: httpParams })
            .pipe(
              map((res: any) => this.normalizeList(res)),
              catchError(() => of([]))
            );
        })
      );
  }

  // Get Detail Data
  getDetail(id: string): Observable<MarketingPengajuanDetailResponse | null> {
    return this.http
      .get<ApiResponse<MarketingPengajuanDetailResponse> | any>(`${this.baseUrl}/${id}`)
      .pipe(
        map((res: any) => res?.data || res || null),
        catchError(() => {
          return this.http
            .get<any>(`${this.fallbackUrl}/${id}`)
            .pipe(
              map((res: any) => res?.data || res || null),
              catchError(() => of(null))
            );
        })
      );
  }

  private normalizeList(res: any): MarketingPengajuanItemResponse[] {
    const rawList = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
    return rawList.map((item: any) => this.normalizeItem(item));
  }

  private normalizeItem(item: any): MarketingPengajuanItemResponse {
    return {
      ...item,
      id: item.id || item.pengajuanId || '',
      pengajuanId: item.pengajuanId || item.id || '',
      noPengajuan: item.noPengajuan || item.nomorPengajuan || '',
      nomorPengajuan: item.noPengajuan || item.nomorPengajuan || '',
      customer: item.customer || item.namaCustomer || item.nama || '',
      namaCustomer: item.customer || item.namaCustomer || item.nama || '',
      jumlah: item.jumlah ?? item.nominalPinjaman ?? item.nominal ?? 0,
      nominalPinjaman: item.jumlah ?? item.nominalPinjaman ?? item.nominal ?? 0,
      status: item.status || item.statusPengajuan || '',
      statusPengajuan: item.statusPengajuan || item.status || '',
      tenor: item.tenor ?? item.tenorBulan ?? 12,
      tenorBulan: item.tenorBulan ?? item.tenor ?? 12,
      tanggalPengajuan: item.tanggalPengajuan || item.createdDate || '',
      createdDate: item.createdDate || item.tanggalPengajuan || '',
    };
  }

  private parseResponse(
    res: any,
    defaultSize: number
  ): PageResponse<MarketingPengajuanItemResponse> {
    let items: any[] = [];
    let totalElements = 0;
    let totalPages = 1;
    let currentPage = 0;
    let pageSize = defaultSize;

    if (res && res.data && typeof res.data === 'object') {
      if (Array.isArray(res.data.content)) {
        items = res.data.content;
        totalElements = res.data.totalElements ?? items.length;
        totalPages = res.data.totalPages ?? Math.ceil(totalElements / defaultSize);
        currentPage = res.data.currentPage ?? res.data.number ?? 0;
        pageSize = res.data.pageSize ?? res.data.size ?? defaultSize;
      } else if (Array.isArray(res.data)) {
        items = res.data;
        totalElements = items.length;
        totalPages = Math.ceil(items.length / defaultSize) || 1;
      }
    } else if (res && Array.isArray(res.content)) {
      items = res.content;
      totalElements = res.totalElements ?? items.length;
      totalPages = res.totalPages ?? Math.ceil(totalElements / defaultSize);
      currentPage = res.currentPage ?? res.number ?? 0;
      pageSize = res.pageSize ?? res.size ?? defaultSize;
    } else if (Array.isArray(res)) {
      items = res;
      totalElements = items.length;
      totalPages = Math.ceil(items.length / defaultSize) || 1;
    }

    const normalizedContent = items.map((item) => this.normalizeItem(item));

    return {
      content: normalizedContent,
      totalElements: totalElements || normalizedContent.length,
      totalPages: totalPages || 1,
      currentPage,
      pageSize,
    };
  }
}
