import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LucideX } from '@lucide/angular';

export type BadgeVariant =
  | 'primary'
  | 'neutral'
  | 'success'
  | 'warning'
  | 'error'
  | 'purple'
  | 'indigo'
  | 'info'
  | 'cyan'
  | 'orange'
  | 'outline'
  | 'solid-primary'
  | 'solid-neutral'
  | 'solid-success'
  | 'solid-warning'
  | 'solid-error';

export type BadgeSize = 'sm' | 'md' | 'lg';
export type BadgeRounded = 'pill' | 'rounded' | 'square';

@Component({
  selector: 'app-badge',
  standalone: true,
  imports: [CommonModule, LucideX],
  templateUrl: './badge.component.html',
  styleUrl: './badge.component.css',
})
export class BadgeComponent {
  @Input() variant: BadgeVariant = 'neutral';
  @Input() size: BadgeSize = 'md';
  @Input() rounded: BadgeRounded = 'pill';
  @Input() dot: boolean = false;
  @Input() dotPulse: boolean = false;
  @Input() removable: boolean = false;
  @Input() wrap: boolean = false;
  @Input() ariaLabel?: string;

  @Output() removed = new EventEmitter<void>();
  @Output() badgeClick = new EventEmitter<MouseEvent>();

  onBadgeClick(event: MouseEvent): void {
    this.badgeClick.emit(event);
  }

  onRemoveClick(event: MouseEvent): void {
    event.stopPropagation();
    event.preventDefault();
    this.removed.emit();
  }

  get badgeClasses(): string {
    const classes: string[] = [
      'inline-flex items-center font-medium tracking-tight select-none transition-colors duration-150',
    ];

    if (this.wrap) {
      classes.push('whitespace-normal text-center leading-snug');
    } else {
      classes.push('whitespace-nowrap');
    }

    // Sizes
    switch (this.size) {
      case 'sm':
        classes.push(this.wrap ? 'px-2 py-1 text-[11px] gap-1' : 'px-2 py-0.5 text-[11px] gap-1');
        break;
      case 'md':
        classes.push('px-2.5 py-0.5 text-xs gap-1.5');
        break;
      case 'lg':
        classes.push('px-3 py-1 text-sm gap-2');
        break;
    }

    // Rounded
    switch (this.rounded) {
      case 'pill':
        classes.push('rounded-full');
        break;
      case 'rounded':
        classes.push('rounded-md');
        break;
      case 'square':
        classes.push('rounded-none');
        break;
    }

    // Variants (Soft/Tint vs Solid vs Outline)
    switch (this.variant) {
      case 'primary':
        classes.push('bg-primary-0 text-primary-70');
        break;
      case 'solid-primary':
        classes.push('bg-primary text-white shadow-xs');
        break;
      case 'neutral':
        classes.push('bg-neutral-0 text-neutral-50');
        break;
      case 'solid-neutral':
        classes.push('bg-neutral-50 text-white shadow-xs');
        break;
      case 'success':
        classes.push('bg-success-0 text-success-70');
        break;
      case 'solid-success':
        classes.push('bg-success text-white shadow-xs');
        break;
      case 'warning':
        classes.push('bg-warning-0 text-warning-80');
        break;
      case 'solid-warning':
        classes.push('bg-warning text-white shadow-xs');
        break;
      case 'error':
        classes.push('bg-error-0 text-error-70');
        break;
      case 'solid-error':
        classes.push('bg-error text-white shadow-xs');
        break;
      case 'purple':
        classes.push('bg-purple-50 text-purple-700');
        break;
      case 'indigo':
        classes.push('bg-indigo-50 text-indigo-700');
        break;
      case 'info':
        classes.push('bg-blue-50 text-blue-700');
        break;
      case 'cyan':
        classes.push('bg-cyan-50 text-cyan-700');
        break;
      case 'orange':
        classes.push('bg-orange-50 text-orange-700');
        break;
      case 'outline':
        classes.push('bg-white text-neutral-50 border border-neutral-200');
        break;
    }

    return classes.join(' ');
  }

  get dotClasses(): string {
    const base = 'w-1.5 h-1.5 rounded-full';
    switch (this.variant) {
      case 'primary':
      case 'solid-primary':
        return `${base} bg-primary`;
      case 'neutral':
      case 'solid-neutral':
      case 'outline':
        return `${base} bg-neutral-40`;
      case 'success':
      case 'solid-success':
        return `${base} bg-success`;
      case 'warning':
      case 'solid-warning':
        return `${base} bg-warning-60`;
      case 'error':
      case 'solid-error':
        return `${base} bg-error`;
      case 'purple':
        return `${base} bg-purple-600`;
      case 'indigo':
        return `${base} bg-indigo-600`;
      case 'info':
        return `${base} bg-blue-600`;
      case 'cyan':
        return `${base} bg-cyan-600`;
      case 'orange':
        return `${base} bg-orange-600`;
    }
  }

  get pulsePingClasses(): string {
    const base = 'absolute inline-flex h-full w-full rounded-full opacity-75 animate-ping';
    switch (this.variant) {
      case 'primary':
      case 'solid-primary':
        return `${base} bg-primary`;
      case 'neutral':
      case 'solid-neutral':
      case 'outline':
        return `${base} bg-neutral-40`;
      case 'success':
      case 'solid-success':
        return `${base} bg-success`;
      case 'warning':
      case 'solid-warning':
        return `${base} bg-warning-60`;
      case 'error':
      case 'solid-error':
        return `${base} bg-error`;
      case 'purple':
        return `${base} bg-purple-600`;
      case 'indigo':
        return `${base} bg-indigo-600`;
      case 'info':
        return `${base} bg-blue-600`;
      case 'cyan':
        return `${base} bg-cyan-600`;
      case 'orange':
        return `${base} bg-orange-600`;
    }
  }
}
