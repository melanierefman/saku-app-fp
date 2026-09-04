import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { CabangService } from './cabang.service';

describe('CabangService', () => {
  let service: CabangService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CabangService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(CabangService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
