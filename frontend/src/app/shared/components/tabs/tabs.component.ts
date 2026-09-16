import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface TabItem {
  key: string;
  label: string;
  count?: number | string;
  dotColor?: string;
  badgeVariant?: 'warning' | 'success' | 'primary' | 'error' | 'neutral' | 'default';
  disabled?: boolean;
}

export type TabsVariant = 'folder' | 'underline' | 'segmented' | 'pill';
export type TabsSize = 'sm' | 'md' | 'lg';

@Component({
  selector: 'app-tabs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tabs.component.html',
  styleUrl: './tabs.component.css',
})
export class TabsComponent {
  @Input() items: TabItem[] = [];
  @Input() activeKey: string = '';
  @Input() variant: TabsVariant = 'folder';
  @Input() size: TabsSize = 'sm';

  @Output() tabChange = new EventEmitter<string>();
  @Output() activeKeyChange = new EventEmitter<string>();
  @Output() itemClick = new EventEmitter<TabItem>();

  onTabClick(item: TabItem): void {
    if (item.disabled) return;
    this.activeKey = item.key;
    this.tabChange.emit(item.key);
    this.activeKeyChange.emit(item.key);
    this.itemClick.emit(item);
  }

  getContainerClass(): string {
    if (this.variant === 'folder') {
      return 'flex items-center gap-1 border-b border-[#E5E7EB] w-full';
    }
    if (this.variant === 'underline') {
      return 'flex items-center gap-6 border-b border-[#E5E7EB] w-full';
    }
    if (this.variant === 'pill') {
      return 'flex flex-wrap items-center gap-2';
    }
    // 'segmented'
    return 'flex flex-wrap items-center gap-1.5 p-1 bg-neutral-0 border border-[#E5E7EB] rounded-xl self-start sm:self-auto';
  }

  getTabClass(item: TabItem): string {
    const isActive = this.activeKey === item.key;

    // 1. Folder / Rounded-Top Variant
    if (this.variant === 'folder') {
      if (item.disabled) {
        return 'px-4 py-2.5 rounded-t-lg text-neutral-30 cursor-not-allowed font-medium text-sm flex items-center gap-2 select-none';
      }
      const base =
        'px-4 py-2.5 rounded-t-lg text-sm font-medium transition-all cursor-pointer flex items-center gap-2 select-none whitespace-nowrap';
      if (isActive) {
        return `${base} bg-[#F3F4F6] text-primary font-medium`;
      }
      return `${base} text-neutral-60 hover:text-neutral-90 hover:bg-neutral-0/80`;
    }

    // 2. Underline Variant
    if (this.variant === 'underline') {
      if (item.disabled) {
        return 'relative pb-3 pt-1 text-sm text-neutral-30 cursor-not-allowed font-medium flex items-center gap-2 select-none';
      }
      const base =
        'relative pb-3 pt-1 text-sm transition-all cursor-pointer flex items-center gap-2 whitespace-nowrap select-none border-b-2 -mb-px';
      if (isActive) {
        return `${base} border-primary text-primary font-medium`;
      }
      return `${base} border-transparent text-neutral-50 hover:text-neutral-90 font-medium`;
    }

    // 3. Pill Variant
    if (this.variant === 'pill') {
      if (item.disabled) {
        return 'px-3.5 py-1.5 rounded-full text-xs text-neutral-30 bg-neutral-0 cursor-not-allowed font-medium';
      }
      const base =
        'px-3.5 py-1.5 rounded-full text-xs transition-all cursor-pointer flex items-center gap-1.5 whitespace-nowrap select-none font-medium';
      if (isActive) {
        return `${base} bg-primary text-white shadow-xs font-medium`;
      }
      return `${base} bg-neutral-0 text-neutral-60 hover:bg-neutral-10 border border-[#E5E7EB]`;
    }

    // 4. Segmented Variant
    const base =
      'transition-all cursor-pointer flex items-center gap-1.5 whitespace-nowrap select-none font-medium';

    let sizeClass = 'px-3 py-1.5 rounded-lg text-xs';
    if (this.size === 'md') {
      sizeClass = 'px-4 py-2 rounded-xl text-sm';
    } else if (this.size === 'lg') {
      sizeClass = 'px-5 py-2.5 rounded-xl text-base';
    }

    if (item.disabled) {
      return `${base} ${sizeClass} text-neutral-30 cursor-not-allowed`;
    }

    if (isActive) {
      if (
        item.badgeVariant === 'warning' ||
        (item.dotColor && item.dotColor.includes('warning'))
      ) {
        return `${base} ${sizeClass} bg-warning-0 text-warning-80 border border-warning-20 shadow-xs font-medium`;
      }
      if (
        item.badgeVariant === 'success' ||
        (item.dotColor && item.dotColor.includes('success'))
      ) {
        return `${base} ${sizeClass} bg-success-0 text-success-70 border border-success-20 shadow-xs font-medium`;
      }
      if (
        item.badgeVariant === 'error' ||
        (item.dotColor && item.dotColor.includes('error'))
      ) {
        return `${base} ${sizeClass} bg-error-0 text-error-70 border border-error-20 shadow-xs font-medium`;
      }
      if (
        item.badgeVariant === 'primary' ||
        (item.dotColor && item.dotColor.includes('primary'))
      ) {
        return `${base} ${sizeClass} bg-primary-0 text-primary border border-primary-20 shadow-xs font-medium`;
      }
      return `${base} ${sizeClass} bg-white text-neutral-90 shadow-xs font-medium`;
    }

    // Inactive
    if (item.dotColor && item.dotColor.includes('warning')) {
      return `${base} ${sizeClass} text-neutral-50 hover:text-warning-80`;
    }
    if (item.dotColor && item.dotColor.includes('success')) {
      return `${base} ${sizeClass} text-neutral-50 hover:text-success-70`;
    }
    if (item.dotColor && item.dotColor.includes('error')) {
      return `${base} ${sizeClass} text-neutral-50 hover:text-error-70`;
    }
    return `${base} ${sizeClass} text-neutral-50 hover:text-neutral-90`;
  }
}
