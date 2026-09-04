import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../models/auth/auth.models';
import { PageResponse } from '../../models/superadmin/karyawan.model';
import {
  PencairanItem,
  PencairanDetail,
  PencairanRequest,
  PencairanResponse,
  AngsuranItem,
} from '../../models/backoffice/pencairan.model';

@Injectable({
  providedIn: 'root',
})
export class BackOfficePencairanService {
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
      if (params.tanggalPencairan) {
        httpParams = httpParams.set('tanggalPencairan', params.tanggalPencairan);
      }
    }

    return this.http.get<ApiResponse<PageResponse<PencairanItem>>>(this.baseUrl, { params: httpParams }).pipe(
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
        console.warn('Backend API /backoffice/pencairan not available or error, using mock data:', err);
        return of(this.getMockPaginatedList(params));
      })
    );
  }

  // 2. Get Detail Pencairan by ID
  findById(id: string): Observable<ApiResponse<PencairanDetail>> {
    return this.http.get<ApiResponse<PencairanDetail>>(`${this.baseUrl}/${id}`).pipe(
      catchError((err) => {
        console.warn(`Backend API /backoffice/pencairan/${id} not available, using mock detail:`, err);
        const mockDetail = this.getMockDetail(id);
        return of({
          statusCode: 200,
          message: 'Detail pencairan berhasil dimuat (mock)',
          data: mockDetail,
        });
      })
    );
  }

  getDetail(id: string): Observable<PencairanDetail> {
    return this.findById(id).pipe(
      map((res) => res.data)
    );
  }

  // 3. Get Angsuran Schedule
  getAngsuran(id: string): Observable<AngsuranItem[]> {
    return this.http.get<ApiResponse<AngsuranItem[]>>(`${this.baseUrl}/${id}/angsuran`).pipe(
      map((res) => res.data || []),
      catchError(() => {
        const detail = this.getMockDetail(id);
        return of(detail.listAngsuran || []);
      })
    );
  }

  // 4. Process Pencairan Dana
  prosesPencairan(id: string, req: PencairanRequest): Observable<ApiResponse<PencairanResponse>> {
    return this.http.post<ApiResponse<PencairanResponse>>(`${this.baseUrl}/${id}/cairkan`, req);
  }

  cairkan(id: string, req: PencairanRequest): Observable<ApiResponse<PencairanResponse>> {
    return this.prosesPencairan(id, req);
  }

  // 5. Get Full File URL
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
  }): PageResponse<PencairanItem> {
    const allMock: PencairanItem[] = [
      {
        pengajuanId: 'PEN-001',
        id: 'PEN-001',
        noPengajuan: 'SAKU-2026-0801',
        nomorPengajuan: 'SAKU-2026-0801',
        customerId: 'CUST-001',
        namaCustomer: 'Ahmad Fauzi',
        namaLengkap: 'Ahmad Fauzi',
        nik: '3201123456780001',
        email: 'ahmad.fauzi@email.com',
        noHp: '081234567890',
        namaBank: 'BCA',
        noRekening: '1234567890',
        namaRekening: 'Ahmad Fauzi',
        jumlahPinjaman: 15000000,
        biayaAdmin: 150000,
        jumlahPencairan: 14850000,
        jumlahPencairanBersih: 14850000,
        tenorBulan: 12,
        bunga: 8,
        namaCabang: 'Cabang Jakarta Pusat',
        statusPengajuan: 'MENUNGGU_PENCAIRAN',
        tanggalDisetujuiBM: '2026-08-30T09:15:00',
        statusPencairan: 'MENUNGGU_PENCAIRAN',
      },
      {
        pengajuanId: 'PEN-002',
        id: 'PEN-002',
        noPengajuan: 'SAKU-2026-0802',
        nomorPengajuan: 'SAKU-2026-0802',
        customerId: 'CUST-002',
        namaCustomer: 'Siti Rahmawati',
        namaLengkap: 'Siti Rahmawati',
        nik: '3201123456780002',
        email: 'siti.rahma@email.com',
        noHp: '081298765432',
        namaBank: 'BCA',
        noRekening: '0987654321',
        namaRekening: 'Siti Rahmawati',
        jumlahPinjaman: 25000000,
        biayaAdmin: 250000,
        jumlahPencairan: 24750000,
        jumlahPencairanBersih: 24750000,
        tenorBulan: 24,
        bunga: 8,
        namaCabang: 'Cabang Bandung',
        statusPengajuan: 'DICAIRKAN',
        tanggalDisetujuiBM: '2026-08-29T14:30:00',
        statusPencairan: 'DICAIRKAN',
        tanggalPencairan: '2026-08-30T10:00:00',
        namaPetugasBackoffice: 'Staff Backoffice 1',
      },
      {
        pengajuanId: 'PEN-003',
        id: 'PEN-003',
        noPengajuan: 'SAKU-2026-0803',
        nomorPengajuan: 'SAKU-2026-0803',
        customerId: 'CUST-003',
        namaCustomer: 'Budi Santoso',
        namaLengkap: 'Budi Santoso',
        nik: '3201123456780003',
        email: 'budi.santoso@email.com',
        noHp: '081311223344',
        namaBank: 'Mandiri',
        noRekening: '5555666677',
        namaRekening: 'Budi Santoso',
        jumlahPinjaman: 10000000,
        biayaAdmin: 100000,
        jumlahPencairan: 9900000,
        jumlahPencairanBersih: 9900000,
        tenorBulan: 6,
        bunga: 8,
        namaCabang: 'Cabang Surabaya',
        statusPengajuan: 'MENUNGGU_PENCAIRAN',
        tanggalDisetujuiBM: '2026-08-28T11:00:00',
        statusPencairan: 'MENUNGGU_PENCAIRAN',
      },
    ];

    let filtered = [...allMock];
    if (params?.search) {
      const q = params.search.toLowerCase();
      filtered = filtered.filter(
        (i) =>
          i.noPengajuan.toLowerCase().includes(q) ||
          i.namaCustomer.toLowerCase().includes(q) ||
          i.nik.includes(q) ||
          i.namaCabang.toLowerCase().includes(q) ||
          i.noRekening.includes(q)
      );
    }
    if (params?.status && params.status !== 'ALL') {
      filtered = filtered.filter((i) => i.statusPencairan === params.status);
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

  private getMockDetail(id: string): PencairanDetail {
    const schedule: AngsuranItem[] = [];
    const tenor = 12;
    const pokok = 15000000 / tenor;
    const bunga = (15000000 * 0.08) / tenor;
    for (let i = 1; i <= tenor; i++) {
      schedule.push({
        id: `ANG-${i}`,
        cicilanKe: i,
        jumlahAngsuran: pokok + bunga,
        jatuhTempo: `2026-${String((i % 12) + 1).padStart(2, '0')}-15`,
        statusBayar: 'BELUM_BAYAR',
      });
    }

    return {
      pengajuanId: id,
      id,
      nomorPengajuan: 'SAKU-2026-0801',
      noPengajuan: 'SAKU-2026-0801',
      tanggalPengajuan: '2026-08-30T08:15:00',
      statusPengajuan: 'MENUNGGU_PENCAIRAN',
      customerId: 'CUST-001',
      namaLengkap: 'Ahmad Fauzi',
      namaCustomer: 'Ahmad Fauzi',
      nik: '3201123456780001',
      email: 'ahmad.fauzi@email.com',
      noHp: '081234567890',
      namaBank: 'BCA',
      noRekening: '1234567890',
      namaRekening: 'Ahmad Fauzi',
      jumlahPinjaman: 15000000,
      tenorBulan: 12,
      tujuanPinjaman: 'Modal Usaha Warung Sembako',
      bunga: 8,
      biayaAdmin: 150000,
      jumlahPencairanBersih: 14850000,
      estimasiAngsuranBulanan: pokok + bunga,
      namaCabang: 'Cabang Jakarta Pusat',
      statusPencairan: 'MENUNGGU_PENCAIRAN',
      fotoKtp: 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop',
      fotoSelfie: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop',
      fotoSlipGaji: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=600&auto=format&fit=crop',
      listAngsuran: schedule,
    };
  }
}

// Alias for backwards compatibility
export { BackOfficePencairanService as PencairanService };
