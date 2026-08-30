import {
  Component,
  ContentChildren,
  Directive,
  Input,
  QueryList,
  TemplateRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideCircleHelp, LucideInbox } from '@lucide/angular';

export interface TableColumn<T = any> {
  key: string;
  header: string;
  width?: string;
  align?: 'left' | 'center' | 'right';
  tooltip?: string;
  headerClass?: string;
  cellClass?: string;
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
  imports: [CommonModule, LucideCircleHelp, LucideInbox],
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
}
