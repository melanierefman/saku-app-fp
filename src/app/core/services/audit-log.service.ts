import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, timeout, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/auth.models';
import { AuditLog, AuditLogPageResponse } from '../models/audit-log.model';

@Injectable({
  providedIn: 'root',
})
export class AuditLogService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/master/audit-log`;
  private readonly fallbackUrl = `${environment.apiUrl}/audit-log`;

  getAuditLogs(params?: {
    action?: string;
    entity?: string;
    karyawanId?: string;
    keyword?: string;
    page?: number;
    size?: number;
  }): Observable<AuditLogPageResponse> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', String(params.page));
      if (params.size !== undefined) httpParams = httpParams.set('size', String(params.size));
      if (params.action) httpParams = httpParams.set('action', params.action);
      if (params.entity) httpParams = httpParams.set('entity', params.entity);
      if (params.karyawanId) httpParams = httpParams.set('karyawanId', params.karyawanId);
      if (params.keyword) httpParams = httpParams.set('keyword', params.keyword);
    }

    return this.http
      .get<ApiResponse<AuditLogPageResponse> | any>(this.baseUrl, { params: httpParams })
      .pipe(
        timeout(6000),
        map((res: any) => this.parsePageResponse(res, params?.size || 10)),
        catchError(() => {
          return this.http
            .get<any>(this.fallbackUrl, { params: httpParams })
            .pipe(
              timeout(6000),
              map((res: any) => this.parsePageResponse(res, params?.size || 10)),
              catchError(() =>
                of({
                  logs: [],
                  content: [],
                  totalElements: 0,
                  totalItems: 0,
                  totalPages: 0,
                  currentPage: 0,
                  pageSize: params?.size || 10,
                })
              )
            );
        })
      );
  }

  private parsePageResponse(res: any, defaultSize: number): AuditLogPageResponse {
    let items: AuditLog[] = [];
    let totalElements = 0;
    let totalPages = 1;
    let currentPage = 0;
    let pageSize = defaultSize;

    if (res && res.data && typeof res.data === 'object') {
      if (Array.isArray(res.data.logs)) {
        items = res.data.logs;
        totalElements = res.data.totalItems ?? res.data.totalElements ?? items.length;
        totalPages = res.data.totalPages ?? Math.ceil(totalElements / defaultSize);
        currentPage = res.data.currentPage ?? res.data.page ?? 0;
        pageSize = res.data.pageSize ?? res.data.size ?? defaultSize;
      } else if (Array.isArray(res.data.content)) {
        items = res.data.content;
        totalElements = res.data.totalElements ?? res.data.totalItems ?? items.length;
        totalPages = res.data.totalPages ?? Math.ceil(totalElements / defaultSize);
        currentPage = res.data.currentPage ?? res.data.page ?? 0;
        pageSize = res.data.pageSize ?? res.data.size ?? defaultSize;
      } else if (Array.isArray(res.data)) {
        items = res.data;
        totalElements = items.length;
        totalPages = Math.ceil(items.length / defaultSize) || 1;
      }
    } else if (res && Array.isArray(res.logs)) {
      items = res.logs;
      totalElements = res.totalItems ?? res.totalElements ?? items.length;
      totalPages = res.totalPages ?? Math.ceil(totalElements / defaultSize);
      currentPage = res.currentPage ?? res.page ?? 0;
      pageSize = res.pageSize ?? res.size ?? defaultSize;
    } else if (res && Array.isArray(res.content)) {
      items = res.content;
      totalElements = res.totalElements ?? items.length;
      totalPages = res.totalPages ?? Math.ceil(totalElements / defaultSize);
      currentPage = res.currentPage ?? res.page ?? 0;
      pageSize = res.pageSize ?? res.size ?? defaultSize;
    } else if (Array.isArray(res)) {
      items = res;
      totalElements = items.length;
      totalPages = Math.ceil(items.length / defaultSize) || 1;
    }

    const normalizedItems = items.map((item) => ({
      ...item,
      karyawanNama: item.karyawan?.nama || item.karyawanNama || item.username || '-',
      username: item.karyawan?.username || item.username || '-',
      role: item.karyawan?.role || item.role || '-',
      timestamp: item.createdDate || item.timestamp,
    }));

    return {
      logs: normalizedItems,
      content: normalizedItems,
      totalItems: totalElements || normalizedItems.length,
      totalElements: totalElements || normalizedItems.length,
      totalPages: totalPages || 1,
      currentPage,
      pageSize,
    };
  }
}
