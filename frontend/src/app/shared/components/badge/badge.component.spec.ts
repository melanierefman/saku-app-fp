import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BadgeComponent } from './badge.component';

describe('BadgeComponent', () => {
  let component: BadgeComponent;
  let fixture: ComponentFixture<BadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BadgeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create badge component', () => {
    expect(component).toBeTruthy();
  });

  it('should compute badgeClasses based on variant and size', () => {
    component.variant = 'success';
    component.size = 'sm';
    component.rounded = 'pill';

    const classes = component.badgeClasses;
    expect(classes).toContain('bg-success-0');
    expect(classes).toContain('rounded-full');
  });

  it('should emit removed event when remove button is clicked', () => {
    const emitSpy = vi.spyOn(component.removed, 'emit');
    const mockEvent = new MouseEvent('click');
    component.onRemoveClick(mockEvent);

    expect(emitSpy).toHaveBeenCalled();
  });
});
