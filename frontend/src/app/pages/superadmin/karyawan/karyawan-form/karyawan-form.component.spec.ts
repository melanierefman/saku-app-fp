import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { KaryawanFormComponent } from './karyawan-form.component';

describe('KaryawanFormComponent', () => {
  let component: KaryawanFormComponent;
  let fixture: ComponentFixture<KaryawanFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KaryawanFormComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(KaryawanFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
