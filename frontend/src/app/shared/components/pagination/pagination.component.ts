import {
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideChevronLeft, LucideChevronRight } from '@lucide/angular';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideChevronLeft, LucideChevronRight],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css',
})
export class PaginationComponent {
  @Input() currentPage: number = 1;
  @Input() totalItems: number = 0;
  @Input() pageSize: number = 10;
  @Input() pageSizeOptions: number[] = [10, 25, 50, 100];
  @Input() showPageSize: boolean = false;
  @Input() showSummary: boolean = true;

  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();

  get safeTotalItems(): number {
    const n = Number(this.totalItems);
    return isNaN(n) || n < 0 ? 0 : n;
  }

  get safePageSize(): number {
    const n = Number(this.pageSize);
    return isNaN(n) || n <= 0 ? 10 : n;
  }

  get safeCurrentPage(): number {
    const n = Number(this.currentPage);
    return isNaN(n) || n < 1 ? 1 : n;
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.safeTotalItems / this.safePageSize));
  }

  get startItem(): number {
    if (this.safeTotalItems === 0) return 0;
    return (this.safeCurrentPage - 1) * this.safePageSize + 1;
  }

  get endItem(): number {
    return Math.min(this.safeTotalItems, this.safeCurrentPage * this.safePageSize);
  }

  get visiblePages(): (number | string)[] {
    const total = this.totalPages;
    const current = this.safeCurrentPage;

    if (total <= 10) {
      return Array.from({ length: total }, (_, i) => i + 1);
    }

    const pages: (number | string)[] = [];

    if (current <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i);
      pages.push('...');
      pages.push(total);
    } else if (current >= total - 3) {
      pages.push(1);
      pages.push('...');
      for (let i = total - 4; i <= total; i++) pages.push(i);
    } else {
      pages.push(1);
      pages.push('...');
      pages.push(current - 1);
      pages.push(current);
      pages.push(current + 1);
      pages.push('...');
      pages.push(total);
    }

    return pages;
  }

  goToPage(page: number | string): void {
    if (typeof page !== 'number') return;
    if (page < 1 || page > this.totalPages || page === this.safeCurrentPage) return;
    this.currentPage = page;
    this.pageChange.emit(this.currentPage);
  }

  onPageSizeChange(newSize: number): void {
    this.pageSize = Number(newSize);
    this.currentPage = 1;
    this.pageSizeChange.emit(this.pageSize);
    this.pageChange.emit(this.currentPage);
  }
}
