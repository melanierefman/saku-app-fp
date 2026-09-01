import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { CabangComponent } from './cabang.component';
import { CabangService } from '../../core';

describe('CabangComponent', () => {
  let component: CabangComponent;
  let fixture: ComponentFixture<CabangComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CabangComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        CabangService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CabangComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
