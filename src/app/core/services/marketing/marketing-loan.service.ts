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
import {
  ReviewPengajuanRequest,
  ReviewPengajuanResponse,
} from '../../models/marketing/marketing-loan.model';

@Injectable({
  providedIn: 'root',
})
export class MarketingLoanService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/marketing/pengajuan-pinjaman`;

  // 1. Get Paginated List
  findAllPaginated(params?: {
    page?: number;
    size?: number;
    search?: string;
    status?: string;
    tanggalPengajuan?: string;
    tanggalReview?: string;
  }): Observable<PageResponse<MarketingPengajuanItemResponse>> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', String(params.page));
      if (params.size !== undefined) httpParams = httpParams.set('size', String(params.size));
      if (params.search) httpParams = httpParams.set('search', params.search);
      if (params.status) httpParams = httpParams.set('status', params.status);
      if (params.tanggalPengajuan) httpParams = httpParams.set('tanggalPengajuan', params.tanggalPengajuan);
      if (params.tanggalReview) httpParams = httpParams.set('tanggalReview', params.tanggalReview);
    }

    return this.http
      .get<ApiResponse<PageResponse<MarketingPengajuanItemResponse>> | any>(this.baseUrl, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => this.parseResponse(res, params?.size || 10)),
        catchError((err) => {
          console.warn('Failed to fetch marketing loan applications:', err);
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
  findAll(status?: string): Observable<MarketingPengajuanItemResponse[]> {
    let httpParams = new HttpParams();
    if (status) httpParams = httpParams.set('status', status);

    return this.http
      .get<ApiResponse<MarketingPengajuanItemResponse[]> | any>(`${this.baseUrl}/all`, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => this.normalizeList(res)),
        catchError(() => of([]))
      );
  }

  private scoreCache = new Map<string, number>();
  private statusScoringCache = new Map<string, string>();

  getScoreFromCache(id: string): number | undefined {
    return this.scoreCache.get(id);
  }

  cacheScore(id: string, score: number, statusScoring?: string): void {
    if (id && score !== undefined && score !== null && !isNaN(score)) {
      this.scoreCache.set(id, score);
      if (statusScoring) this.statusScoringCache.set(id, statusScoring);
    }
  }

  // 3. Get Detail By ID
  getDetail(id: string): Observable<MarketingPengajuanDetailResponse | null> {
    return this.http
      .get<ApiResponse<MarketingPengajuanDetailResponse> | any>(`${this.baseUrl}/${id}`)
      .pipe(
        map((res: any) => {
          const detail = res?.data || res || null;
          if (detail) {
            const rawScore =
              detail.skorKredit ??
              detail.skor ??
              detail.scoring?.skorKredit ??
              detail.scoring?.skor ??
              detail.customer?.skorKredit;
            if (rawScore !== undefined && rawScore !== null && !isNaN(Number(rawScore))) {
              this.cacheScore(id, Number(rawScore), detail.statusScoring);
            }
          }
          return detail;
        }),
        catchError((err) => {
          console.warn('Failed to fetch marketing detail loan:', err);
          return of(null);
        })
      );
  }

  // 4. Submit Review Decision (PUT /api/marketing/pengajuan-pinjaman/{id})
  review(
    id: string,
    payload: ReviewPengajuanRequest
  ): Observable<ApiResponse<ReviewPengajuanResponse>> {
    return this.http.put<ApiResponse<ReviewPengajuanResponse>>(
      `${this.baseUrl}/${id}`,
      payload
    );
  }

  private normalizeList(res: any): MarketingPengajuanItemResponse[] {
    const rawList = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
    return rawList.map((item: any) => this.normalizeItem(item));
  }

  private normalizeItem(item: any): MarketingPengajuanItemResponse {
    const id = item.id || item.pengajuanId || '';
    const cachedScore = id ? this.scoreCache.get(id) : undefined;
    const cachedStatusScoring = id ? this.statusScoringCache.get(id) : undefined;

    const rawScore =
      item.skorKredit ??
      item.skor ??
      item.scoring?.skorKredit ??
      item.scoring?.skor ??
      item.scoring?.score ??
      item.scoring?.totalSkor ??
      item.creditScore ??
      item.score ??
      item.nilaiSkor ??
      item.customer?.skorKredit ??
      item.customer?.skor ??
      item.customer?.creditScore ??
      cachedScore;

    if (id && rawScore !== undefined && rawScore !== null && !isNaN(Number(rawScore))) {
      this.cacheScore(id, Number(rawScore), item.statusScoring || cachedStatusScoring);
    }

    return {
      ...item,
      id,
      pengajuanId: id,
      noPengajuan: item.noPengajuan || item.nomorPengajuan || '',
      nomorPengajuan: item.noPengajuan || item.nomorPengajuan || '',
      customer: item.customer || item.namaCustomer || item.nama || '',
      namaCustomer: item.customer || item.namaCustomer || item.nama || '',
      jumlah: item.jumlah ?? item.nominalPinjaman ?? item.nominal ?? 0,
      nominalPinjaman: item.jumlah ?? item.nominalPinjaman ?? item.nominal ?? 0,
      skorKredit: rawScore !== undefined && rawScore !== null ? Number(rawScore) : undefined,
      skor: rawScore !== undefined && rawScore !== null ? Number(rawScore) : undefined,
      statusScoring: item.statusScoring || cachedStatusScoring,
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
