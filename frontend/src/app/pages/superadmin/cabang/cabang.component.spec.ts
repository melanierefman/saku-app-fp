import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { CabangComponent } from './cabang.component';
import { CabangService } from '../../../core';
import { ToastService } from '../../../shared/components';
import { environment } from '../../../../environments/environment';

describe('CabangComponent (Integration Test)', () => {
  let component: CabangComponent;
  let fixture: ComponentFixture<CabangComponent>;
  let httpMock: HttpTestingController;
  let toastService: ToastService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CabangComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        CabangService,
        ToastService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CabangComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    toastService = TestBed.inject(ToastService);

    fixture.detectChanges();

    const initReq = httpMock.expectOne(`${environment.apiUrl}/cabang`);
    initReq.flush({
      data: [],
      message: 'Success',
      statusCode: 200,
    });
  });

  afterEach(() => {
    // Memastikan tidak ada request HTTP yang menggantung
    httpMock.verify();
  });

  it('should create component successfully', () => {
    expect(component).toBeTruthy();
  });

  // Integration Test: Berhasil tambah cabang dengan modal
  it('Positif: should successfully submit new cabang form and refresh list', () => {
    // Buka modal tambah cabang
    component.openCreateModal();
    expect(component.isFormModalOpen()).toBe(true);

    // Isi data form cabang
    component.formNama = 'Cabang Semarang';
    component.formKota = 'Semarang';
    component.formIsDefault = false;
    component.formStatus = true;

    // Klik tombol simpan, modal terbuka
    component.saveCabang();
    expect(component.isConfirmModalOpen()).toBe(true);

    // Konfirmasi simpan, send req POST
    component.confirmSubmitCabang();
    expect(component.isSubmitting()).toBe(true);

    // Response sukses dari BE
    const postReq = httpMock.expectOne(`${environment.apiUrl}/cabang`);
    expect(postReq.request.method).toBe('POST');
    expect(postReq.request.body).toEqual({
      nama: 'Cabang Semarang',
      kota: 'Semarang',
      isDefault: false,
      status: true,
    });

    postReq.flush({
      data: {
        id: 'semarang-123',
        nama: 'Cabang Semarang',
        kota: 'Semarang',
        isDefault: false,
        status: true,
      },
      message: 'Success',
      statusCode: 200,
    });

    // Refresh data
    const refreshReq = httpMock.expectOne(`${environment.apiUrl}/cabang`);
    expect(refreshReq.request.method).toBe('GET');
    refreshReq.flush({
      data: [
        {
          id: 'semarang-123',
          nama: 'Cabang Semarang',
          kota: 'Semarang',
          isDefault: false,
          status: true,
        },
      ],
      message: 'Success',
      statusCode: 200,
    });

    expect(component.isSubmitting()).toBe(false);
    expect(component.isFormModalOpen()).toBe(false);
    expect(component.isConfirmModalOpen()).toBe(false);
    expect(component.allCabang().length).toBe(1);
    expect(component.allCabang()[0].nama).toBe('Cabang Semarang');
  });

  // Integration Test: Validasi form gagal saat nama/kota kosong
  it('Negatif: should show validation errors and NOT send POST request when form is invalid', () => {
    // Buka modal tambah cabang
    component.openCreateModal();
    expect(component.isFormModalOpen()).toBe(true);

    // Kosongkan field yang wajib diisi (nama & kota)
    component.formNama = '';
    component.formKota = '';
    component.formStatus = true;

    // Klik tombol simpan
    component.saveCabang();

    // Verifikasi error validasi muncul
    expect(component.formNamaError).toBe('Nama cabang wajib diisi');
    expect(component.formKotaError).toBe('Kota cabang wajib diisi');

    // Modal konfirmasi tidak terbuka
    expect(component.isConfirmModalOpen()).toBe(false);

    // Tidak ada req POST
    httpMock.expectNone(`${environment.apiUrl}/cabang`);
  });
});
