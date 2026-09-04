import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { KaryawanService } from './karyawan.service';

describe('KaryawanService', () => {
  let service: KaryawanService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        KaryawanService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(KaryawanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
