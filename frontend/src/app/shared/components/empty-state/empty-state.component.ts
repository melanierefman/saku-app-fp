import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [CommonModule, ButtonComponent],
  templateUrl: './empty-state.component.html',
  styleUrl: './empty-state.component.css',
})
export class EmptyStateComponent {
  @Input() title: string = 'Tidak Ada Data';
  @Input() message: string = 'Belum ada data yang tersedia untuk ditampilkan saat ini.';
  @Input() actionText?: string;
  @Input() compact: boolean = false;

  @Output() action = new EventEmitter<void>();

  onActionClick(): void {
    this.action.emit();
  }
}
