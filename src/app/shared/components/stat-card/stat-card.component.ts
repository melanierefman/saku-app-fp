import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SkeletonComponent } from '../skeleton/skeleton.component';

export type StatCardVariant =
  | 'primary'
  | 'success'
  | 'warning'
  | 'error'
  | 'neutral'
  | 'info';

export type StatCardSize = 'sm' | 'md' | 'lg';

export interface StatTrend {
  value: string;
  isPositive?: boolean;
  label?: string;
}

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule, SkeletonComponent],
  templateUrl: './stat-card.component.html',
  styleUrl: './stat-card.component.css',
})
export class StatCardComponent {
  @Input() title: string = '';
  @Input() value: string | number | null | undefined = '-';
  @Input() subtitle?: string;
  @Input() variant: StatCardVariant = 'primary';
  @Input() size: StatCardSize = 'sm';
  @Input() trend?: StatTrend | null;
  @Input() badge?: string;
  @Input() badgeVariant: StatCardVariant = 'neutral';
  @Input() loading: boolean = false;
  @Input() tooltip?: string;

  get formattedValue(): string {
    if (this.value === null || this.value === undefined || this.value === '') {
      return '-';
    }
    return String(this.value);
  }

  get iconBoxClasses(): string {
    const sizeClasses = this.size === 'lg' ? 'w-11 h-11' : this.size === 'md' ? 'w-10 h-10' : 'w-9 h-9';
    
    switch (this.variant) {
      case 'success':
        return `${sizeClasses} rounded-xl flex items-center justify-center shrink-0 bg-success-0 text-success-70 border border-success-20/50`;
      case 'warning':
        return `${sizeClasses} rounded-xl flex items-center justify-center shrink-0 bg-warning-0 text-warning-80 border border-warning-20/50`;
      case 'error':
        return `${sizeClasses} rounded-xl flex items-center justify-center shrink-0 bg-error-0 text-error-70 border border-error-20/50`;
      case 'neutral':
        return `${sizeClasses} rounded-xl flex items-center justify-center shrink-0 bg-neutral-10/60 text-neutral-60 border border-neutral-20/50`;
      case 'info':
        return `${sizeClasses} rounded-xl flex items-center justify-center shrink-0 bg-blue-50 text-blue-600 border border-blue-200/50`;
      case 'primary':
      default:
        return `${sizeClasses} rounded-xl flex items-center justify-center shrink-0 bg-primary/10 text-primary border border-primary/20`;
    }
  }

  get valueTextClasses(): string {
    switch (this.size) {
      case 'lg':
        return 'text-2xl font-bold text-neutral-90 tracking-tight truncate';
      case 'md':
        return 'text-xl font-bold text-neutral-90 tracking-tight truncate';
      case 'sm':
      default:
        return 'text-base font-bold text-neutral-90 tracking-tight truncate';
    }
  }

  get containerClasses(): string {
    const pad = this.size === 'lg' ? 'p-5' : this.size === 'md' ? 'p-4' : 'p-3.5';
    return `${pad} rounded-xl border border-[#E5E7EB] bg-white flex items-center justify-between gap-2.5 shadow-2xs hover:border-neutral-20 transition-all min-w-0`;
  }
}
