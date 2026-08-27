import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

export interface BreadcrumbItem {
  label: string;
  url?: string;
  active?: boolean;
}

@Component({
  selector: 'app-breadcrumbs',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './breadcrumbs.component.html',
  styleUrl: './breadcrumbs.component.css',
})
export class BreadcrumbsComponent {
  @Input() items: BreadcrumbItem[] = [];
  @Output() itemClick = new EventEmitter<BreadcrumbItem>();

  onItemClick(item: BreadcrumbItem, event: MouseEvent): void {
    if (item.active || !item.url) {
      event.preventDefault();
    }
    this.itemClick.emit(item);
  }

  isLastItem(index: number): boolean {
    return index === this.items.length - 1;
  }
}
