import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { RoleAccessComponent } from './role-access.component';

describe('RoleAccessComponent', () => {
  let component: RoleAccessComponent;
  let fixture: ComponentFixture<RoleAccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoleAccessComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RoleAccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
