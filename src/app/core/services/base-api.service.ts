import { inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, timeout, of, catchError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/auth.models';
import { PageResponse } from '../models/karyawan.model';

export abstract class BaseApiService<
  T,
  CreateDto = Partial<T>,
  UpdateDto = Partial<T>
> {
  protected http = inject(HttpClient);
  protected baseUrl = environment.apiUrl;
  protected abstract endpoint: string;

  protected get fullUrl(): string {
    const base = this.baseUrl.replace(/\/+$/, '');
    const path = this.endpoint.replace(/^\/+/, '');
    return `${base}/${path}`;
  }

  protected buildHttpParams(params?: Record<string, any>): HttpParams {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach((key) => {
        const val = params[key];
        if (val !== undefined && val !== null && val !== '') {
          httpParams = httpParams.set(key, String(val));
        }
      });
    }
    return httpParams;
  }

  // Get Paginated & Filtered
  getPaginated(params?: Record<string, any>): Observable<PageResponse<T>> {
    const httpParams = this.buildHttpParams(params);

    return this.http
      .get<any>(this.fullUrl, { params: httpParams })
      .pipe(
        timeout(5000),
        map((res: any) => this.parsePageResponse(res, params?.['pageSize'] || 10)),
        catchError(() => {
          return this.http.get<any>(this.fullUrl).pipe(
            timeout(5000),
            map((res: any) => this.parsePageResponse(res, params?.['pageSize'] || 10)),
            catchError(() =>
              of({
                content: [],
                totalElements: 0,
                totalPages: 0,
                currentPage: 0,
                pageSize: 10,
              })
            )
          );
        })
      );
  }

  private parsePageResponse(res: any, defaultPageSize: number = 10): PageResponse<T> {
    if (res && res.data && typeof res.data === 'object') {
      if (Array.isArray(res.data.content)) {
        return res.data as PageResponse<T>;
      }
      if (Array.isArray(res.data)) {
        return {
          content: res.data,
          totalElements: res.data.length,
          totalPages: Math.ceil(res.data.length / defaultPageSize) || 1,
          currentPage: 0,
          pageSize: defaultPageSize,
        };
      }
    }
    if (res && Array.isArray(res.content)) {
      return res as PageResponse<T>;
    }
    if (Array.isArray(res)) {
      return {
        content: res,
        totalElements: res.length,
        totalPages: Math.ceil(res.length / defaultPageSize) || 1,
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

  // Get All Unpaginated
  getAll(params?: Record<string, any>): Observable<T[]> {
    const httpParams = this.buildHttpParams(params);

    return this.http
      .get<any>(this.fullUrl, { params: httpParams })
      .pipe(
        timeout(5000),
        map((res: any) => {
          if (res && typeof res === 'object' && 'data' in res) {
            if (Array.isArray(res.data)) {
              return res.data;
            }
            if (res.data && Array.isArray(res.data.content)) {
              return res.data.content;
            }
          }
          if (res && Array.isArray(res.content)) {
            return res.content;
          }
          return Array.isArray(res) ? res : [];
        })
      );
  }

  // Get All Without Pagination
  getAllUnpaginated(): Observable<T[]> {
    return this.http
      .get<any>(`${this.fullUrl}/all`)
      .pipe(
        timeout(5000),
        map((res: any) => {
          if (res && typeof res === 'object' && 'data' in res && Array.isArray(res.data)) {
            return res.data;
          }
          return Array.isArray(res) ? res : [];
        })
      );
  }

  // Get By Id
  getById(id: string | number): Observable<T> {
    return this.http.get<ApiResponse<T> | T>(`${this.fullUrl}/${id}`).pipe(
      timeout(5000),
      map((res) => {
        if (res && typeof res === 'object' && 'data' in res) {
          return (res as ApiResponse<T>).data;
        }
        return res as T;
      })
    );
  }

  // Create
  create(payload: CreateDto): Observable<T> {
    return this.http.post<ApiResponse<T> | T>(this.fullUrl, payload).pipe(
      map((res) => {
        if (res && typeof res === 'object' && 'data' in res) {
          return (res as ApiResponse<T>).data;
        }
        return res as T;
      })
    );
  }

  // Update
  update(payload: UpdateDto): Observable<T> {
    const id = (payload as any)?.id;
    const url = id ? `${this.fullUrl}/${id}` : this.fullUrl;

    return this.http.put<ApiResponse<T> | T>(url, payload).pipe(
      map((res) => {
        if (res && typeof res === 'object' && 'data' in res) {
          return (res as ApiResponse<T>).data;
        }
        return res as T;
      })
    );
  }

  // Update By Id
  updateById(id: string | number, payload: UpdateDto): Observable<T> {
    return this.http
      .put<ApiResponse<T> | T>(`${this.fullUrl}/${id}`, payload)
      .pipe(
        map((res) => {
          if (res && typeof res === 'object' && 'data' in res) {
            return (res as ApiResponse<T>).data;
          }
          return res as T;
        })
      );
  }

  // Delete By Id
  delete(id: string | number): Observable<any> {
    return this.http.delete<ApiResponse<any> | any>(`${this.fullUrl}/${id}`).pipe(
      map((res) => {
        if (res && typeof res === 'object' && 'data' in res) {
          return (res as ApiResponse<any>).data;
        }
        return res;
      })
    );
  }
}
