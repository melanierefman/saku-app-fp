import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BackOfficeVerifikasiCustomerService } from './back-office-verifikasi-customer.service';

describe('BackOfficeVerifikasiCustomerService', () => {
  let service: BackOfficeVerifikasiCustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BackOfficeVerifikasiCustomerService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(BackOfficeVerifikasiCustomerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
