import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { PencairanListComponent } from './pencairan-list.component';
import { PencairanService } from '../../../../core';

describe('PencairanListComponent', () => {
  let component: PencairanListComponent;
  let fixture: ComponentFixture<PencairanListComponent>;
  let pencairanServiceSpy: { findAllPaginated: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    pencairanServiceSpy = {
      findAllPaginated: vi.fn().mockReturnValue(
        of({
          statusCode: 200,
          data: { content: [], totalElements: 0 },
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [PencairanListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: PencairanService, useValue: pencairanServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PencairanListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create pencairan list component', () => {
    expect(component).toBeTruthy();
  });
});
