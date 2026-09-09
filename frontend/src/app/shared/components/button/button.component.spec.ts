import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ButtonComponent } from './button.component';

describe('ButtonComponent', () => {
  let component: ButtonComponent;
  let fixture: ComponentFixture<ButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ButtonComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create button component', () => {
    expect(component).toBeTruthy();
  });

  it('should compute buttonClasses for primary variant and md size', () => {
    component.variant = 'primary';
    component.size = 'md';
    const classes = component.buttonClasses;
    expect(classes).toContain('bg-primary');
    expect(classes).toContain('h-10');
  });

  it('should emit btnClick when not disabled and not loading', () => {
    const emitSpy = vi.spyOn(component.btnClick, 'emit');
    const mockEvent = new MouseEvent('click');
    component.onClick(mockEvent);
    expect(emitSpy).toHaveBeenCalledWith(mockEvent);
  });

  it('should NOT emit btnClick when disabled or loading', () => {
    const emitSpy = vi.spyOn(component.btnClick, 'emit');
    const mockEvent = new MouseEvent('click');
    component.disabled = true;
    component.onClick(mockEvent);
    expect(emitSpy).not.toHaveBeenCalled();

    component.disabled = false;
    component.loading = true;
    component.onClick(mockEvent);
    expect(emitSpy).not.toHaveBeenCalled();
  });
});
