import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MonitoringPengajuanService } from './monitoring-pengajuan.service';

describe('MonitoringPengajuanService', () => {
  let service: MonitoringPengajuanService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MonitoringPengajuanService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(MonitoringPengajuanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
