import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { MarketingDashboardComponent } from './marketing-dashboard.component';
import { MarketingDashboardService, MarketingLoanService } from '../../../core';

describe('MarketingDashboardComponent', () => {
  let component: MarketingDashboardComponent;
  let fixture: ComponentFixture<MarketingDashboardComponent>;
  let dashboardServiceSpy: { getDashboardStats: ReturnType<typeof vi.fn> };
  let loanServiceSpy: { findAllPaginated: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    dashboardServiceSpy = {
      getDashboardStats: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: {
            totalPengajuan: 10,
            menungguReview: 3,
            disetujui: 5,
            ditolak: 2,
          },
        })
      ),
    };
    loanServiceSpy = {
      findAllPaginated: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: {
            content: [],
            totalElements: 0,
          },
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [MarketingDashboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: MarketingDashboardService, useValue: dashboardServiceSpy },
        { provide: MarketingLoanService, useValue: loanServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MarketingDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create marketing dashboard component', () => {
    expect(component).toBeTruthy();
  });
});
