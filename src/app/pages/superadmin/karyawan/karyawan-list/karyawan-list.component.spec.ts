import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { KaryawanListComponent } from './karyawan-list.component';

describe('KaryawanListComponent', () => {
  let component: KaryawanListComponent;
  let fixture: ComponentFixture<KaryawanListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KaryawanListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(KaryawanListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
