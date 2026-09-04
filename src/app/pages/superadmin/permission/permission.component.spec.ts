import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { PermissionComponent } from './permission.component';

describe('PermissionComponent', () => {
  let component: PermissionComponent;
  let fixture: ComponentFixture<PermissionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermissionComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PermissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
