import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { BranchManagerDashboardComponent } from './branch-manager-dashboard.component';
import {
  BranchManagerDashboardService,
  BranchManagerApprovalService,
} from '../../../core';

describe('BranchManagerDashboardComponent', () => {
  let component: BranchManagerDashboardComponent;
  let fixture: ComponentFixture<BranchManagerDashboardComponent>;
  let dashboardServiceSpy: { getDashboardStats: ReturnType<typeof vi.fn> };
  let approvalServiceSpy: { findAllPaginated: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    dashboardServiceSpy = {
      getDashboardStats: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: {
            totalPengajuanCabang: 15,
            menungguPersetujuan: 4,
            disetujui: 10,
            ditolak: 1,
          },
        })
      ),
    };
    approvalServiceSpy = {
      findAllPaginated: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: { content: [], totalElements: 0 },
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [BranchManagerDashboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: BranchManagerDashboardService, useValue: dashboardServiceSpy },
        { provide: BranchManagerApprovalService, useValue: approvalServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BranchManagerDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create branch manager dashboard component', () => {
    expect(component).toBeTruthy();
  });
});
