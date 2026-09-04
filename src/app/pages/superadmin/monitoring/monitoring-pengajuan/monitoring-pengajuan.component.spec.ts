import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach } from 'vitest';
import { MonitoringPengajuanComponent } from './monitoring-pengajuan.component';
import { MonitoringPengajuanService, CabangService } from '../../../../core';
import { ToastService } from '../../../../shared/components';

describe('MonitoringPengajuanComponent', () => {
  let component: MonitoringPengajuanComponent;
  let fixture: ComponentFixture<MonitoringPengajuanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitoringPengajuanComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        MonitoringPengajuanService,
        CabangService,
        ToastService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MonitoringPengajuanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component successfully', () => {
    expect(component).toBeTruthy();
  });
});
