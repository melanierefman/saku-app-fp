import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { PlafondFormComponent } from './plafond-form.component';
import { PlafondService } from '../../../core';

describe('PlafondFormComponent', () => {
  let component: PlafondFormComponent;
  let fixture: ComponentFixture<PlafondFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlafondFormComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        PlafondService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PlafondFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
