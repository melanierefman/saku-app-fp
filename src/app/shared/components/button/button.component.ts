import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ButtonVariant =
  | 'primary'
  | 'secondary'
  | 'neutral'
  | 'outline'
  | 'ghost'
  | 'success'
  | 'warning'
  | 'error';

export type ButtonSize = 'xs' | 'sm' | 'md' | 'lg' | 'xl';
export type ButtonRounded = 'none' | 'sm' | 'md' | 'lg' | 'full';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './button.component.html',
  styleUrl: './button.component.css',
})
export class ButtonComponent {
  @Input() variant: ButtonVariant = 'primary';
  @Input() size: ButtonSize = 'md';
  @Input() rounded: ButtonRounded = 'md';
  @Input() disabled: boolean = false;
  @Input() loading: boolean = false;
  @Input() fullWidth: boolean = false;
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() ariaLabel?: string;

  @Output() btnClick = new EventEmitter<MouseEvent>();

  onClick(event: MouseEvent): void {
    if (this.disabled || this.loading) {
      event.preventDefault();
      event.stopPropagation();
      return;
    }
    this.btnClick.emit(event);
  }

  get buttonClasses(): string {
    const classes: string[] = [
      'inline-flex items-center justify-center font-medium transition-all duration-150 select-none cursor-pointer',
      'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2',
    ];

    // Width
    if (this.fullWidth) {
      classes.push('w-full');
    }

    // Disabled & Loading States
    if (this.disabled || this.loading) {
      classes.push('opacity-60 cursor-not-allowed pointer-events-none');
    } else {
      classes.push('active:scale-[0.98]');
    }

    // Sizes
    switch (this.size) {
      case 'xs':
        classes.push('px-2.5 py-1 text-xs gap-1.5 min-h-[28px]');
        break;
      case 'sm':
        classes.push('px-3 py-1.5 text-xs gap-1.5 min-h-[32px]');
        break;
      case 'md':
        classes.push('px-4 py-2 text-sm gap-2 min-h-[40px]');
        break;
      case 'lg':
        classes.push('px-5 py-2.5 text-base gap-2.5 min-h-[46px]');
        break;
      case 'xl':
        classes.push('px-6 py-3.5 text-lg gap-3 min-h-[52px]');
        break;
    }

    // Rounded
    switch (this.rounded) {
      case 'none':
        classes.push('rounded-none');
        break;
      case 'sm':
        classes.push('rounded-sm');
        break;
      case 'md':
        classes.push('rounded-lg');
        break;
      case 'lg':
        classes.push('rounded-xl');
        break;
      case 'full':
        classes.push('rounded-full');
        break;
    }

    // Variants
    switch (this.variant) {
      case 'primary':
        classes.push(
          'bg-primary text-white shadow-sm hover:bg-primary-60 active:bg-primary-70 focus-visible:ring-primary/50'
        );
        break;
      case 'secondary':
        classes.push(
          'bg-primary-0 text-primary-60 border border-primary-20 hover:bg-primary-10 active:bg-primary-20 focus-visible:ring-primary/40'
        );
        break;
      case 'neutral':
        classes.push(
          'bg-neutral-50 text-white shadow-sm hover:bg-neutral-60 active:bg-neutral-70 focus-visible:ring-neutral-40'
        );
        break;
      case 'outline':
        classes.push(
          'border border-neutral-20 text-neutral-50 bg-white hover:bg-neutral-0 active:bg-neutral-10 focus-visible:ring-neutral-30'
        );
        break;
      case 'ghost':
        classes.push(
          'text-neutral-50 bg-transparent hover:bg-neutral-0 active:bg-neutral-10 focus-visible:ring-neutral-20'
        );
        break;
      case 'success':
        classes.push(
          'bg-success text-white shadow-sm hover:bg-success-60 active:bg-success-70 focus-visible:ring-success/50'
        );
        break;
      case 'warning':
        classes.push(
          'bg-warning text-white shadow-sm hover:bg-warning-60 active:bg-warning-70 focus-visible:ring-warning/50'
        );
        break;
      case 'error':
        classes.push(
          'bg-error text-white shadow-sm hover:bg-error-60 active:bg-error-70 focus-visible:ring-error/50'
        );
        break;
    }

    return classes.join(' ');
  }
}
