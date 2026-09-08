import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SuperadminDashboardService } from './superadmin-dashboard.service';

describe('SuperadminDashboardService', () => {
  let service: SuperadminDashboardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SuperadminDashboardService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(SuperadminDashboardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
