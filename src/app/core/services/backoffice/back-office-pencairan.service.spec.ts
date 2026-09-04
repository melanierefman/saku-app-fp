import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BackOfficePencairanService } from './back-office-pencairan.service';

describe('BackOfficePencairanService', () => {
  let service: BackOfficePencairanService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BackOfficePencairanService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(BackOfficePencairanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
