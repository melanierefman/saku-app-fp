import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { VerifikasiCustomerListComponent } from './verifikasi-customer-list.component';
import { VerifikasiCustomerService } from '../../../../core';

describe('VerifikasiCustomerListComponent', () => {
  let component: VerifikasiCustomerListComponent;
  let fixture: ComponentFixture<VerifikasiCustomerListComponent>;
  let kycServiceSpy: { findAllPaginated: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    kycServiceSpy = {
      findAllPaginated: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: { content: [], totalElements: 0 },
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [VerifikasiCustomerListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: VerifikasiCustomerService, useValue: kycServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VerifikasiCustomerListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create verifikasi customer list component', () => {
    expect(component).toBeTruthy();
  });
});
