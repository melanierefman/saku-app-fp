import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { BackofficeDashboardComponent } from './backoffice-dashboard.component';
import {
  BackofficeDashboardService,
  BackOfficeVerifikasiCustomerService,
  BackOfficePencairanService,
} from '../../../core';

describe('BackofficeDashboardComponent', () => {
  let component: BackofficeDashboardComponent;
  let fixture: ComponentFixture<BackofficeDashboardComponent>;
  let dashboardServiceSpy: { getDashboardStats: ReturnType<typeof vi.fn> };
  let kycServiceSpy: { findAllPaginated: ReturnType<typeof vi.fn> };
  let pencairanServiceSpy: { findAllPaginated: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    dashboardServiceSpy = {
      getDashboardStats: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: {
            menungguVerifikasiKyc: 2,
            siapDicairkan: 3,
            totalNominalDicairkan: 50000000,
          },
        })
      ),
    };
    kycServiceSpy = {
      findAllPaginated: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: { content: [], totalElements: 0 },
        })
      ),
    };
    pencairanServiceSpy = {
      findAllPaginated: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: { content: [], totalElements: 0 },
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [BackofficeDashboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: BackofficeDashboardService, useValue: dashboardServiceSpy },
        { provide: BackOfficeVerifikasiCustomerService, useValue: kycServiceSpy },
        { provide: BackOfficePencairanService, useValue: pencairanServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BackofficeDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create backoffice dashboard component', () => {
    expect(component).toBeTruthy();
  });
});
