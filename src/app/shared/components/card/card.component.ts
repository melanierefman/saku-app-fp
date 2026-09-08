import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type CardIconVariant =
  | 'primary'
  | 'neutral'
  | 'success'
  | 'warning'
  | 'error'
  | 'info'
  | 'purple'
  | 'indigo';

export type CardPadding = 'none' | 'sm' | 'md' | 'lg';
export type CardShadow = 'none' | 'xs' | 'sm' | 'md';
export type CardRounded = 'lg' | 'xl' | '2xl' | '3xl';

import { SkeletonComponent } from '../skeleton/skeleton.component';

@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule, SkeletonComponent],
  templateUrl: './card.component.html',
  styleUrl: './card.component.css',
})
export class CardComponent {
  @Input() title?: string;
  @Input() subtitle?: string;
  @Input() showIcon: boolean = true;
  @Input() iconVariant: CardIconVariant = 'primary';
  @Input() padding: CardPadding = 'md';
  @Input() shadow: CardShadow = 'xs';
  @Input() rounded: CardRounded | 'none' = '2xl';
  @Input() bordered: boolean = true;
  @Input() headerBorder: boolean = true;
  @Input() loading: boolean = false;
  @Input() bodyClass: string = '';
  @Input() customClass: string = '';

  get containerClasses(): string {
    const classes: string[] = ['bg-white transition-all duration-200'];

    // Border
    if (this.bordered) {
      classes.push('border border-[#E5E7EB]');
    }

    // Rounded
    switch (this.rounded) {
      case 'lg':
        classes.push('rounded-lg');
        break;
      case 'xl':
        classes.push('rounded-xl');
        break;
      case '2xl':
        classes.push('rounded-2xl');
        break;
      case '3xl':
        classes.push('rounded-3xl');
        break;
    }

    // Shadow
    switch (this.shadow) {
      case 'none':
        break;
      case 'xs':
        classes.push('shadow-xs');
        break;
      case 'sm':
        classes.push('shadow-sm');
        break;
      case 'md':
        classes.push('shadow-md');
        break;
    }

    // Padding
    switch (this.padding) {
      case 'none':
        break;
      case 'sm':
        classes.push('p-4');
        break;
      case 'md':
        classes.push('p-6');
        break;
      case 'lg':
        classes.push('p-8');
        break;
    }

    if (this.customClass) {
      classes.push(this.customClass);
    }

    return classes.join(' ');
  }

  get iconContainerClasses(): string {
    const base =
      'w-8 h-8 rounded-xl flex items-center justify-center shrink-0 transition-colors';

    switch (this.iconVariant) {
      case 'primary':
        return `${base} bg-primary-10 text-primary border border-primary-20/40`;
      case 'success':
        return `${base} bg-success-0 text-success-70 border border-success-20/50`;
      case 'warning':
        return `${base} bg-warning-0 text-warning-80 border border-warning-20/50`;
      case 'error':
        return `${base} bg-error-0 text-error-70 border border-error-20/50`;
      case 'info':
        return `${base} bg-blue-50 text-blue-700 border border-blue-200/50`;
      case 'purple':
        return `${base} bg-purple-50 text-purple-700 border border-purple-200/50`;
      case 'indigo':
        return `${base} bg-indigo-50 text-indigo-700 border border-indigo-200/50`;
      case 'neutral':
      default:
        return `${base} bg-neutral-10 text-neutral-60 border border-neutral-20/50`;
    }
  }
}
