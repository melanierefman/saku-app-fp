import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MarketingLoanService } from './marketing-loan.service';

describe('MarketingLoanService', () => {
  let service: MarketingLoanService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MarketingLoanService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(MarketingLoanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
