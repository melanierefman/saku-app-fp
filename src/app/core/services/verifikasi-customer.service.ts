import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/auth.models';
import { PageResponse } from '../models/karyawan.model';
import {
  VerifikasiCustomerItem,
  VerifikasiCustomerDetail,
  VerifikasiCustomerRequest,
  VerifikasiCustomerResponse,
} from '../models/verifikasi-customer.model';

@Injectable({
  providedIn: 'root',
})
export class VerifikasiCustomerService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/backoffice/verifikasi-customer`;

  // 1. Get Paginated List
  findAllPaginated(params?: {
    page?: number;
    size?: number;
    search?: string;
    status?: string;
    tanggalRegister?: string;
    tanggalPengajuan?: string;
    tanggalVerifikasi?: string;
  }): Observable<PageResponse<VerifikasiCustomerItem>> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', String(params.page));
      if (params.size !== undefined) httpParams = httpParams.set('size', String(params.size));
      if (params.search) httpParams = httpParams.set('search', params.search);
      if (params.status && params.status !== 'ALL') httpParams = httpParams.set('status', params.status);
      const regDate = params.tanggalRegister || params.tanggalPengajuan;
      if (regDate) {
        httpParams = httpParams.set('tanggalRegister', regDate);
        httpParams = httpParams.set('tanggalPengajuan', regDate);
      }
      if (params.tanggalVerifikasi) httpParams = httpParams.set('tanggalVerifikasi', params.tanggalVerifikasi);
    }

    return this.http
      .get<ApiResponse<PageResponse<VerifikasiCustomerItem>> | any>(this.baseUrl, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => this.parseResponse(res, params?.size || 10)),
        catchError((err) => {
          console.warn('Failed to fetch verification customer list:', err);
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

  // 2. Get All List (Unpaginated)
  findAll(status?: string): Observable<VerifikasiCustomerItem[]> {
    let httpParams = new HttpParams();
    if (status && status !== 'ALL') httpParams = httpParams.set('status', status);

    return this.http
      .get<ApiResponse<VerifikasiCustomerItem[]> | any>(`${this.baseUrl}/all`, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => this.normalizeList(res)),
        catchError(() => of([]))
      );
  }

  // 3. Get Pending List Only (PENDING)
  findPending(): Observable<VerifikasiCustomerItem[]> {
    return this.http
      .get<ApiResponse<VerifikasiCustomerItem[]> | any>(`${this.baseUrl}/pending`)
      .pipe(
        map((res: any) => this.normalizeList(res)),
        catchError(() => this.findAll('PENDING'))
      );
  }

  // 4. Get Detail By ID
  getDetail(customerId: string): Observable<VerifikasiCustomerDetail | null> {
    return this.http
      .get<ApiResponse<VerifikasiCustomerDetail> | any>(`${this.baseUrl}/${customerId}`)
      .pipe(
        map((res: any) => res?.data || res || null),
        catchError((err) => {
          console.warn('Failed to fetch verification customer detail:', err);
          return of(null);
        })
      );
  }

  // 4. Submit Verification Decision (PUT /api/backoffice/verifikasi-customer/{customerId})
  verifikasi(
    customerId: string,
    payload: VerifikasiCustomerRequest
  ): Observable<ApiResponse<VerifikasiCustomerResponse>> {
    return this.http.put<ApiResponse<VerifikasiCustomerResponse>>(
      `${this.baseUrl}/${customerId}`,
      payload
    );
  }

  // Helper: File / Image URL resolution
  getFileUrl(path: string | null | undefined): string {
    if (!path) return '';
    if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
      return path;
    }
    const cleanPath = path.replace(/^\/+/, '');
    const baseUrl = environment.apiUrl.replace(/\/api\/?$/, '');
    return `${baseUrl}/uploads/${cleanPath}`;
  }

  private normalizeList(res: any): VerifikasiCustomerItem[] {
    const rawList = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
    return rawList.map((item: any) => this.normalizeItem(item));
  }

  private normalizeItem(item: any): VerifikasiCustomerItem {
    return {
      customerId: item.customerId || item.id,
      id: item.id || item.customerId,
      namaCustomer: item.namaCustomer || item.namaLengkap || item.nama || item.customer || '-',
      namaLengkap: item.namaLengkap || item.namaCustomer,
      nik: item.nik || '-',
      email: item.email || '-',
      noHp: item.noHp || item.nomorHp || item.phone || '-',
      tanggalRegister: item.tanggalRegister || item.tanggalPengajuan || item.createdDate,
      tanggalPengajuan: item.tanggalPengajuan || item.tanggalRegister || item.createdDate,
      createdDate: item.createdDate || item.tanggalRegister || item.tanggalPengajuan,
      statusVerifikasi: item.statusVerifikasi || item.status || 'PENDING',
      catatanVerifikasi: item.catatanVerifikasi || item.catatan,
      tanggalVerifikasi: item.tanggalVerifikasi || item.updatedDate,
    };
  }

  private parseResponse(
    res: any,
    defaultPageSize: number
  ): PageResponse<VerifikasiCustomerItem> {
    const pageData = res?.data || res;
    if (pageData && Array.isArray(pageData.content)) {
      return {
        content: pageData.content.map((item: any) => this.normalizeItem(item)),
        totalElements: pageData.totalElements ?? pageData.content.length,
        totalPages: pageData.totalPages ?? 1,
        currentPage: pageData.currentPage ?? pageData.pageable?.pageNumber ?? 0,
        pageSize: pageData.pageSize ?? pageData.pageable?.pageSize ?? defaultPageSize,
      };
    }
    if (Array.isArray(pageData)) {
      return {
        content: pageData.map((item: any) => this.normalizeItem(item)),
        totalElements: pageData.length,
        totalPages: 1,
        currentPage: 0,
        pageSize: defaultPageSize,
      };
    }
    return {
      content: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      pageSize: defaultPageSize,
    };
  }
}
