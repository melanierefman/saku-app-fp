import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmptyStateComponent } from './empty-state.component';

describe('EmptyStateComponent', () => {
  let component: EmptyStateComponent;
  let fixture: ComponentFixture<EmptyStateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmptyStateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EmptyStateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create empty state component', () => {
    expect(component).toBeTruthy();
    expect(component.title).toBe('Tidak Ada Data');
  });

  it('should emit action event on click', () => {
    const emitSpy = vi.spyOn(component.action, 'emit');
    component.onActionClick();
    expect(emitSpy).toHaveBeenCalled();
  });
});
