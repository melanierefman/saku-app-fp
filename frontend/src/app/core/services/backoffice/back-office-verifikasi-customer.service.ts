import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../models/auth/auth.models';
import { PageResponse } from '../../models/superadmin/karyawan.model';
import {
  VerifikasiCustomerItem,
  VerifikasiCustomerDetail,
  VerifikasiCustomerRequest,
  VerifikasiCustomerResponse,
} from '../../models/backoffice/verifikasi-customer.model';

@Injectable({
  providedIn: 'root',
})
export class BackOfficeVerifikasiCustomerService {
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
      if (params.tanggalVerifikasi) {
        httpParams = httpParams.set('tanggalVerifikasi', params.tanggalVerifikasi);
      }
    }

    return this.http.get<ApiResponse<PageResponse<VerifikasiCustomerItem>>>(this.baseUrl, { params: httpParams }).pipe(
      map((res) => {
        if (res?.data) {
          return res.data;
        }
        return {
          content: [],
          totalElements: 0,
          totalPages: 0,
          currentPage: params?.page || 0,
          pageSize: params?.size || 10,
          isFirst: true,
          isLast: true,
        };
      }),
      catchError((err) => {
        console.warn('Backend API /backoffice/verifikasi-customer not available or error, using mock data:', err);
        return of(this.getMockPaginatedList(params));
      })
    );
  }

  // 2. Get Detail by ID
  findById(id: string): Observable<ApiResponse<VerifikasiCustomerDetail>> {
    return this.http.get<ApiResponse<VerifikasiCustomerDetail>>(`${this.baseUrl}/${id}`).pipe(
      catchError((err) => {
        console.warn(`Backend API /backoffice/verifikasi-customer/${id} not available, using mock detail:`, err);
        const mockDetail = this.getMockDetail(id);
        return of({
          statusCode: 200,
          message: 'Detail customer berhasil dimuat (mock)',
          data: mockDetail,
        });
      })
    );
  }

  getDetail(id: string): Observable<VerifikasiCustomerDetail> {
    return this.findById(id).pipe(
      map((res) => res.data)
    );
  }

  // 3. Process Verification (APPROVE / PERLU_REVISI / REJECT)
  prosesVerifikasi(id: string, req: VerifikasiCustomerRequest): Observable<ApiResponse<VerifikasiCustomerResponse>> {
    return this.http.post<ApiResponse<VerifikasiCustomerResponse>>(`${this.baseUrl}/${id}/verifikasi`, req);
  }

  verifikasi(id: string, req: VerifikasiCustomerRequest): Observable<ApiResponse<VerifikasiCustomerResponse>> {
    return this.prosesVerifikasi(id, req);
  }

  // 4. Get Full File URL
  getFileUrl(path?: string | null): string {
    if (!path) return '';
    if (path.startsWith('http://') || path.startsWith('https://')) {
      return path;
    }
    const cleanPath = path.startsWith('/') ? path : `/${path}`;
    return `${environment.apiUrl}/files${cleanPath}`;
  }

  // MOCK DATA FALLBACKS
  private getMockPaginatedList(params?: {
    page?: number;
    size?: number;
    search?: string;
    status?: string;
  }): PageResponse<VerifikasiCustomerItem> {
    const allMock: VerifikasiCustomerItem[] = [
      {
        customerId: 'CUST-001',
        id: 'CUST-001',
        namaLengkap: 'Ahmad Fauzi',
        namaCustomer: 'Ahmad Fauzi',
        nik: '3201123456780001',
        email: 'ahmad.fauzi@email.com',
        noHp: '081234567890',
        tanggalPengajuan: '2026-08-30T08:15:00',
        tanggalRegister: '2026-08-30T08:15:00',
        statusVerifikasi: 'PENDING',
      },
      {
        customerId: 'CUST-002',
        id: 'CUST-002',
        namaLengkap: 'Siti Rahmawati',
        namaCustomer: 'Siti Rahmawati',
        nik: '3201123456780002',
        email: 'siti.rahma@email.com',
        noHp: '081298765432',
        tanggalPengajuan: '2026-08-29T10:30:00',
        tanggalRegister: '2026-08-29T10:30:00',
        statusVerifikasi: 'APPROVED',
        catatanVerifikasi: 'Berkas lengkap dan sesuai.',
      },
      {
        customerId: 'CUST-003',
        id: 'CUST-003',
        namaLengkap: 'Budi Santoso',
        namaCustomer: 'Budi Santoso',
        nik: '3201123456780003',
        email: 'budi.santoso@email.com',
        noHp: '081311223344',
        tanggalPengajuan: '2026-08-28T14:20:00',
        tanggalRegister: '2026-08-28T14:20:00',
        statusVerifikasi: 'PERLU_REVISI',
        catatanVerifikasi: 'Foto KTP buram, mohon upload ulang yang lebih jelas.',
      },
    ];

    let filtered = [...allMock];
    if (params?.search) {
      const q = params.search.toLowerCase();
      filtered = filtered.filter(
        (i) =>
          i.namaCustomer.toLowerCase().includes(q) ||
          i.nik.includes(q) ||
          i.email.toLowerCase().includes(q) ||
          i.noHp.includes(q)
      );
    }
    if (params?.status && params.status !== 'ALL') {
      filtered = filtered.filter((i) => i.statusVerifikasi === params.status);
    }

    const page = params?.page || 0;
    const size = params?.size || 10;
    const start = page * size;
    const end = start + size;
    const content = filtered.slice(start, end);

    return {
      content,
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / size) || 1,
      currentPage: page,
      pageSize: size,
      isFirst: page === 0,
      isLast: end >= filtered.length,
    };
  }

  private getMockDetail(id: string): VerifikasiCustomerDetail {
    return {
      customerId: id,
      id,
      namaLengkap: 'Ahmad Fauzi',
      namaCustomer: 'Ahmad Fauzi',
      nik: '3201123456780001',
      email: 'ahmad.fauzi@email.com',
      noHp: '081234567890',
      username: 'ahmadfauzi',
      pekerjaan: 'Karyawan Swasta',
      statusPekerjaan: 'KARYAWAN_TETAP',
      tempatKerja: 'PT Maju Bersama Sejahtera',
      penghasilanBulanan: 8500000,
      pendapatan: 8500000,
      namaBank: 'BCA',
      noRekening: '1234567890',
      namaRekening: 'Ahmad Fauzi',
      statusVerifikasi: 'PENDING',
      tanggalPengajuan: '2026-08-30T08:15:00',
      tanggalRegister: '2026-08-30T08:15:00',
      fotoKtp: 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop',
      fotoSelfie: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop',
    };
  }
}

// Alias for backwards compatibility
export { BackOfficeVerifikasiCustomerService as VerifikasiCustomerService };
