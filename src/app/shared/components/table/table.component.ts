import {
  Component,
  ContentChildren,
  Directive,
  EventEmitter,
  Input,
  Output,
  QueryList,
  TemplateRef,
  signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucideCircleHelp,
  LucideInbox,
  LucideChevronUp,
  LucideChevronDown,
  LucideChevronsUpDown,
} from '@lucide/angular';

export interface TableColumn<T = any> {
  key: string;
  header: string;
  width?: string;
  minWidth?: string;
  align?: 'left' | 'center' | 'right';
  tooltip?: string;
  headerClass?: string;
  cellClass?: string;
  sticky?: 'left' | 'right';
  sortable?: boolean;
}

@Directive({
  selector: '[appTableCell]',
  standalone: true,
})
export class TableCellDirective {
  @Input('appTableCell') columnKey: string = '';
  constructor(public templateRef: TemplateRef<any>) {}
}

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [
    CommonModule,
    LucideCircleHelp,
    LucideInbox,
    LucideChevronUp,
    LucideChevronDown,
    LucideChevronsUpDown,
  ],
  templateUrl: './table.component.html',
  styleUrl: './table.component.css',
})
export class TableComponent {
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() loading: boolean = false;
  @Input() skeletonRows: number = 5;
  @Input() emptyMessage: string = 'Tidak ada data yang ditemukan.';
  @Input() rowKey: string = 'id';
  @Input() minWidth?: string;
  @Input() sortKey?: string;
  @Input() sortDirection: 'asc' | 'desc' | '' = '';

  @Output() sortChange = new EventEmitter<{ key: string; direction: 'asc' | 'desc' }>();

  isScrolled = signal<boolean>(false);

  @ContentChildren(TableCellDirective) cellTemplates!: QueryList<TableCellDirective>;

  getTemplate(columnKey: string): TemplateRef<any> | null {
    if (!this.cellTemplates) return null;
    const match = this.cellTemplates.find((t) => t.columnKey === columnKey);
    return match ? match.templateRef : null;
  }

  get dummySkeletonRows(): number[] {
    return Array.from({ length: this.skeletonRows }, (_, i) => i);
  }

  getAlignClass(align?: 'left' | 'center' | 'right'): string {
    switch (align) {
      case 'center':
        return 'text-center';
      case 'right':
        return 'text-right';
      case 'left':
      default:
        return 'text-left';
    }
  }

  onScroll(event: Event): void {
    const target = event.target as HTMLElement;
    if (target) {
      this.isScrolled.set(target.scrollLeft > 2);
    }
  }

  onHeaderClick(col: TableColumn): void {
    if (!col.sortable) return;
    let nextDir: 'asc' | 'desc' = 'asc';
    if (this.sortKey === col.key) {
      nextDir = this.sortDirection === 'asc' ? 'desc' : 'asc';
    }
    this.sortKey = col.key;
    this.sortDirection = nextDir;
    this.sortChange.emit({ key: col.key, direction: nextDir });
  }

  getStickyStyle(colIndex: number, col: TableColumn): Record<string, string> {
    if (!col.sticky) return {};

    const style: Record<string, string> = {
      position: 'sticky',
    };

    if (col.sticky === 'left') {
      let offset = 0;
      for (let i = 0; i < colIndex; i++) {
        const prevCol = this.columns[i];
        if (prevCol.sticky === 'left') {
          const widthVal = parseInt(prevCol.width || '150', 10);
          offset += isNaN(widthVal) ? 150 : widthVal;
        }
      }
      style['left'] = `${offset}px`;
    } else if (col.sticky === 'right') {
      let offset = 0;
      for (let i = this.columns.length - 1; i > colIndex; i--) {
        const nextCol = this.columns[i];
        if (nextCol.sticky === 'right') {
          const widthVal = parseInt(nextCol.width || '120', 10);
          offset += isNaN(widthVal) ? 120 : widthVal;
        }
      }
      style['right'] = `${offset}px`;
    }

    return style;
  }

  isLastStickyLeft(colIndex: number): boolean {
    const col = this.columns[colIndex];
    if (col?.sticky !== 'left') return false;
    const nextCol = this.columns[colIndex + 1];
    return !nextCol || nextCol.sticky !== 'left';
  }

  isFirstStickyRight(colIndex: number): boolean {
    const col = this.columns[colIndex];
    if (col?.sticky !== 'right') return false;
    const prevCol = this.columns[colIndex - 1];
    return !prevCol || prevCol.sticky !== 'right';
  }
}
