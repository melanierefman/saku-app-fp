import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { PengajuanPinjamanListComponent } from './pengajuan-pinjaman-list.component';
import { MarketingLoanService } from '../../../../core';

describe('PengajuanPinjamanListComponent', () => {
  let component: PengajuanPinjamanListComponent;
  let fixture: ComponentFixture<PengajuanPinjamanListComponent>;
  let loanServiceSpy: { findAllPaginated: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    loanServiceSpy = {
      findAllPaginated: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: { content: [], totalElements: 0 },
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [PengajuanPinjamanListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: MarketingLoanService, useValue: loanServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PengajuanPinjamanListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create pengajuan pinjaman list component', () => {
    expect(component).toBeTruthy();
  });
});
