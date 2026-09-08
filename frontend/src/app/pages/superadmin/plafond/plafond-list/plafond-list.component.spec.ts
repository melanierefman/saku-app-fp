import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { PlafondListComponent } from './plafond-list.component';
import { PlafondService } from '../../../../core';

describe('PlafondListComponent', () => {
  let component: PlafondListComponent;
  let fixture: ComponentFixture<PlafondListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlafondListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        PlafondService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PlafondListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
