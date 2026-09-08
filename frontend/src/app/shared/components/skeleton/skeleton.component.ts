import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type SkeletonVariant = 'text' | 'circle' | 'rect' | 'card' | 'button';
export type SkeletonRounded = 'none' | 'sm' | 'md' | 'lg' | 'xl' | '2xl' | 'full';

@Component({
  selector: 'app-skeleton',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './skeleton.component.html',
  styleUrl: './skeleton.component.css',
})
export class SkeletonComponent {
  @Input() variant: SkeletonVariant = 'text';
  @Input() width: string = '';
  @Input() height: string = '';
  @Input() rounded?: SkeletonRounded;
  @Input() animate: boolean = true;
  @Input() count: number = 1;
  @Input() customClass: string = '';

  get countArray(): number[] {
    const n = Math.max(1, Math.floor(this.count || 1));
    return Array.from({ length: n }, (_, i) => i);
  }

  getItemClasses(): string {
    const classes: string[] = ['bg-[#E5E7EB] shrink-0 block select-none'];

    if (this.animate) {
      classes.push('animate-pulse');
    }

    // Default shapes by variant
    switch (this.variant) {
      case 'circle':
        classes.push(this.width || 'w-10');
        classes.push(this.height || 'h-10');
        classes.push(this.getRoundedClass('full'));
        break;

      case 'card':
        classes.push(this.width || 'w-full');
        classes.push(this.height || 'h-40');
        classes.push(this.getRoundedClass(this.rounded || '2xl'));
        classes.push('border border-[#E5E7EB]');
        break;

      case 'button':
        classes.push(this.width || 'w-24');
        classes.push(this.height || 'h-9');
        classes.push(this.getRoundedClass(this.rounded || 'xl'));
        break;

      case 'rect':
        classes.push(this.width || 'w-full');
        classes.push(this.height || 'h-16');
        classes.push(this.getRoundedClass(this.rounded || 'xl'));
        break;

      case 'text':
      default:
        classes.push(this.width || 'w-full');
        classes.push(this.height || 'h-4');
        classes.push(this.getRoundedClass(this.rounded || 'md'));
        break;
    }

    if (this.customClass) {
      classes.push(this.customClass);
    }

    return classes.join(' ');
  }

  private getRoundedClass(rounded: SkeletonRounded): string {
    switch (rounded) {
      case 'none':
        return 'rounded-none';
      case 'sm':
        return 'rounded-sm';
      case 'md':
        return 'rounded-md';
      case 'lg':
        return 'rounded-lg';
      case 'xl':
        return 'rounded-xl';
      case '2xl':
        return 'rounded-2xl';
      case 'full':
        return 'rounded-full';
      default:
        return 'rounded-md';
    }
  }
}
