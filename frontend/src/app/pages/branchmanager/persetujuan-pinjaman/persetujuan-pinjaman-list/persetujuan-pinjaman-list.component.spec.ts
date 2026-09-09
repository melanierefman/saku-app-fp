import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { PersetujuanPinjamanListComponent } from './persetujuan-pinjaman-list.component';
import { BranchManagerApprovalService } from '../../../../core';

describe('PersetujuanPinjamanListComponent', () => {
  let component: PersetujuanPinjamanListComponent;
  let fixture: ComponentFixture<PersetujuanPinjamanListComponent>;
  let approvalServiceSpy: { findAllPaginated: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    approvalServiceSpy = {
      findAllPaginated: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: { content: [], totalElements: 0 },
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [PersetujuanPinjamanListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: BranchManagerApprovalService, useValue: approvalServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PersetujuanPinjamanListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create persetujuan pinjaman list component', () => {
    expect(component).toBeTruthy();
  });
});
