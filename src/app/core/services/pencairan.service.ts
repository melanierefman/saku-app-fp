import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/auth.models';
import { PageResponse } from '../models/karyawan.model';
import {
  PencairanItem,
  PencairanDetail,
  PencairanRequest,
  PencairanResponse,
  AngsuranItem,
} from '../models/pencairan.model';

@Injectable({
  providedIn: 'root',
})
export class PencairanService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/backoffice/pencairan`;

  // 1. Get Paginated List Pencairan
  findAllPaginated(params?: {
    page?: number;
    size?: number;
    search?: string;
    status?: string;
    tanggalDisetujuiBM?: string;
    tanggalPersetujuan?: string;
    tanggalPencairan?: string;
  }): Observable<PageResponse<PencairanItem>> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', String(params.page));
      if (params.size !== undefined) httpParams = httpParams.set('size', String(params.size));
      if (params.search) httpParams = httpParams.set('search', params.search);
      if (params.status && params.status !== 'ALL') httpParams = httpParams.set('status', params.status);
      const tglBM = params.tanggalDisetujuiBM || params.tanggalPersetujuan;
      if (tglBM) {
        httpParams = httpParams.set('tanggalDisetujuiBM', tglBM);
        httpParams = httpParams.set('tanggalPersetujuan', tglBM);
      }
      if (params.tanggalPencairan) httpParams = httpParams.set('tanggalPencairan', params.tanggalPencairan);
    }

    return this.http
      .get<ApiResponse<PageResponse<PencairanItem>> | any>(this.baseUrl, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => this.parseResponse(res, params?.size || 10)),
        catchError((err) => {
          console.warn('Failed to fetch pencairan list:', err);
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

  // 2. Get All Unpaginated
  findAll(status?: string): Observable<PencairanItem[]> {
    let httpParams = new HttpParams();
    if (status && status !== 'ALL') httpParams = httpParams.set('status', status);

    return this.http
      .get<ApiResponse<PencairanItem[]> | any>(`${this.baseUrl}/all`, {
        params: httpParams,
      })
      .pipe(
        map((res: any) => this.normalizeList(res)),
        catchError(() => of([]))
      );
  }

  // 3. Get Pending Pencairan Only (MENUNGGU_PENCAIRAN)
  findPending(): Observable<PencairanItem[]> {
    return this.http
      .get<ApiResponse<PencairanItem[]> | any>(`${this.baseUrl}/pending`)
      .pipe(
        map((res: any) => this.normalizeList(res)),
        catchError(() => this.findAll('MENUNGGU_PENCAIRAN'))
      );
  }

  // 4. Get Detail By ID
  getDetail(pengajuanId: string): Observable<PencairanDetail | null> {
    return this.http
      .get<ApiResponse<PencairanDetail> | any>(`${this.baseUrl}/${pengajuanId}`)
      .pipe(
        map((res: any) => res?.data || res || null),
        catchError((err) => {
          console.warn('Failed to fetch pencairan detail:', err);
          return of(null);
        })
      );
  }

  // 5. Eksekusi Pencairan Dana (Cairkan)
  cairkan(
    pengajuanId: string,
    payload: PencairanRequest = {}
  ): Observable<ApiResponse<PencairanResponse>> {
    return this.http
      .post<ApiResponse<PencairanResponse>>(
        `${this.baseUrl}/${pengajuanId}/cairkan`,
        payload
      )
      .pipe(
        catchError(() => {
          return this.http
            .post<ApiResponse<PencairanResponse>>(
              `${this.baseUrl}/${pengajuanId}`,
              payload
            )
            .pipe(
              catchError(() => {
                return this.http.put<ApiResponse<PencairanResponse>>(
                  `${this.baseUrl}/${pengajuanId}`,
                  payload
                );
              })
            );
        })
      );
  }

  // 6. Get Jadwal Angsuran Saja
  getAngsuran(pengajuanId: string): Observable<AngsuranItem[]> {
    return this.http
      .get<ApiResponse<AngsuranItem[]> | any>(`${this.baseUrl}/${pengajuanId}/angsuran`)
      .pipe(
        map((res: any) => (Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [])),
        catchError(() => of([]))
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

  private normalizeList(res: any): PencairanItem[] {
    const rawList = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
    return rawList.map((item: any) => this.normalizeItem(item));
  }

  private normalizeItem(item: any): PencairanItem {
    const jumlahPinjaman = item.jumlahPinjaman ?? item.nominalPinjaman ?? item.nominal ?? 0;
    const biayaAdmin = item.biayaAdmin ?? item.biayaAdministrasi ?? 0;
    const jumlahPencairan =
      item.jumlahPencairan ?? item.jumlahPencairanBersih ?? Math.max(0, jumlahPinjaman - biayaAdmin);

    return {
      pengajuanId: item.pengajuanId || item.id,
      id: item.id || item.pengajuanId,
      noPengajuan: item.noPengajuan || item.nomorPengajuan || '-',
      nomorPengajuan: item.nomorPengajuan || item.noPengajuan || '-',
      customerId: item.customerId || item.id || '-',
      namaCustomer: item.namaCustomer || item.namaLengkap || item.customer || '-',
      namaLengkap: item.namaLengkap || item.namaCustomer,
      nik: item.nik || '-',
      email: item.email || '-',
      noHp: item.noHp || item.nomorHp || '-',
      namaBank: item.namaBank || item.bank || '-',
      noRekening: item.noRekening || item.nomorRekening || '-',
      namaRekening: item.namaRekening || item.namaPemilikRekening || item.namaCustomer || '-',
      jumlahPinjaman,
      biayaAdmin,
      jumlahPencairan,
      jumlahPencairanBersih: jumlahPencairan,
      tenorBulan: item.tenorBulan ?? item.tenor ?? 0,
      bunga: item.bunga ?? item.sukuBunga ?? 0,
      namaCabang:
        typeof item.cabang === 'object' ? item.cabang?.namaCabang || item.cabang?.nama : item.namaCabang || item.cabang || '-',
      statusPengajuan: item.statusPengajuan || item.status || '-',
      tanggalDisetujuiBM: item.tanggalDisetujuiBM || item.tanggalPersetujuanBM || item.tanggalPersetujuan,
      statusPencairan: item.statusPencairan || (item.isDisbursed ? 'DICAIRKAN' : 'MENUNGGU_PENCAIRAN'),
      tanggalPencairan: item.tanggalPencairan || item.disbursementDate,
      namaPetugasBackoffice: item.namaPetugasBackoffice || item.disbursedBy,
    };
  }

  private parseResponse(
    res: any,
    defaultPageSize: number
  ): PageResponse<PencairanItem> {
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
