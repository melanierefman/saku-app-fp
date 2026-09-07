import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../models/auth/auth.models';
import { PageResponse } from '../../models/superadmin/karyawan.model';
import {
  BranchManagerPengajuanItemResponse,
  BranchManagerPengajuanDetailResponse,
  PersetujuanPinjamanRequest,
  PersetujuanPinjamanResponse,
} from '../../models/branchmanager/branch-manager-approval.model';

@Injectable({
  providedIn: 'root',
})
export class BranchManagerApprovalService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/branch-manager/persetujuan`;

  // 1. Get Paginated List
  findAllPaginated(params?: {
    page?: number;
    size?: number;
    search?: string;
    status?: string;
    tanggalPengajuan?: string;
    tanggalReviewMarketing?: string;
  }): Observable<PageResponse<BranchManagerPengajuanItemResponse>> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', String(params.page));
      if (params.size !== undefined) httpParams = httpParams.set('size', String(params.size));
      if (params.search) httpParams = httpParams.set('search', params.search);
      if (params.status) httpParams = httpParams.set('status', params.status);
      if (params.tanggalPengajuan) httpParams = httpParams.set('tanggalPengajuan', params.tanggalPengajuan);
      if (params.tanggalReviewMarketing)
        httpParams = httpParams.set('tanggalReviewMarketing', params.tanggalReviewMarketing);
    }

    return this.http
      .get<ApiResponse<PageResponse<BranchManagerPengajuanItemResponse>> | any>(this.baseUrl, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => this.parseResponse(res, params?.size || 10)),
        catchError((err) => {
          console.warn('Failed to fetch BM approval applications:', err);
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
  findAll(status?: string): Observable<BranchManagerPengajuanItemResponse[]> {
    let httpParams = new HttpParams();
    if (status) httpParams = httpParams.set('status', status);

    return this.http
      .get<ApiResponse<BranchManagerPengajuanItemResponse[]> | any>(`${this.baseUrl}/all`, {
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
  getDetail(id: string): Observable<BranchManagerPengajuanDetailResponse | null> {
    return this.http
      .get<ApiResponse<BranchManagerPengajuanDetailResponse> | any>(`${this.baseUrl}/${id}`)
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
          console.warn('Failed to fetch BM approval detail:', err);
          return of(null);
        })
      );
  }

  // 4. Submit BM Decision (PUT /api/branch-manager-approval/{id})
  persetujuan(
    id: string,
    payload: PersetujuanPinjamanRequest
  ): Observable<ApiResponse<PersetujuanPinjamanResponse>> {
    return this.http.put<ApiResponse<PersetujuanPinjamanResponse>>(
      `${this.baseUrl}/${id}`,
      payload
    );
  }

  private normalizeList(res: any): BranchManagerPengajuanItemResponse[] {
    const rawList = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
    return rawList.map((item: any) => this.normalizeItem(item));
  }

  private normalizeItem(item: any): BranchManagerPengajuanItemResponse {
    const id = item.pengajuanId || item.id || '';
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
      pengajuanId: id,
      id,
      nomorPengajuan: item.nomorPengajuan || item.noPengajuan,
      noPengajuan: item.noPengajuan || item.nomorPengajuan,
      customerId: item.customerId,
      namaCustomer: item.namaCustomer || item.customer || item.nama || item.namaLengkap,
      customer: item.customer || item.namaCustomer || item.namaLengkap,
      email: item.email,
      noHp: item.noHp,
      jumlahPinjaman: item.jumlahPinjaman ?? item.jumlah ?? item.nominalPinjaman ?? item.nominal,
      tenorBulan: item.tenorBulan ?? item.tenor,
      namaCabang:
        typeof item.cabang === 'object'
          ? item.cabang?.nama || item.cabang?.namaCabang
          : item.namaCabang || item.cabangNama || item.cabang || 'Kantor Pusat',
      tanggalPengajuan: item.tanggalPengajuan || item.createdDate,
      statusPengajuan: item.statusPengajuan || item.status,
      status: item.status || item.statusPengajuan,
      hasilReviewMarketing: item.hasilReviewMarketing,
      catatanMarketing: item.catatanMarketing,
      tanggalReviewMarketing: item.tanggalReviewMarketing,
      hasilPersetujuanTerakhir: item.hasilPersetujuanTerakhir || item.hasilPersetujuanBM,
      catatanPersetujuanTerakhir: item.catatanPersetujuanTerakhir || item.catatanBM,
      tanggalPersetujuanTerakhir: item.tanggalPersetujuanTerakhir || item.tanggalPersetujuanBM || item.tanggalPersetujuan,
      tanggalPersetujuan: item.tanggalPersetujuan || item.tanggalPersetujuanBM || item.tanggalPersetujuanTerakhir,
      tanggalPersetujuanBM: item.tanggalPersetujuanBM || item.tanggalPersetujuan || item.tanggalPersetujuanTerakhir,
      skorKredit: rawScore !== undefined && rawScore !== null ? Number(rawScore) : undefined,
      skor: rawScore !== undefined && rawScore !== null ? Number(rawScore) : undefined,
      statusScoring: item.statusScoring || item.scoring?.statusScoring || cachedStatusScoring,
    };
  }

  private parseResponse(
    res: any,
    defaultPageSize: number
  ): PageResponse<BranchManagerPengajuanItemResponse> {
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

// Backward compatibility alias
export { BranchManagerApprovalService as BranchManagerPersetujuanService };
