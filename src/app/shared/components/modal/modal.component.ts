import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucideX,
  LucideCircleAlert,
  LucideCircleX,
  LucideCircleCheck,
} from '@lucide/angular';
import { ButtonComponent, ButtonVariant } from '../button/button.component';

export type ModalVariant = 'form' | 'confirm' | 'custom';
export type ModalSize = 'sm' | 'md' | 'lg' | 'xl' | 'full';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [
    CommonModule,
    ButtonComponent,
    LucideX,
    LucideCircleAlert,
    LucideCircleX,
    LucideCircleCheck,
  ],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.css',
})
export class ModalComponent {
  @Input() isOpen: boolean = false;
  @Input() title?: string;
  @Input() message?: string;
  @Input() variant: ModalVariant = 'form';
  @Input() size: ModalSize = 'md';
  @Input() confirmText: string = 'Confirm';
  @Input() cancelText?: string;
  @Input() confirmVariant: ButtonVariant = 'primary';
  @Input() cancelVariant: ButtonVariant = 'cancel';
  @Input() showCloseButton: boolean = true;
  @Input() showFooter: boolean = true;
  @Input() showConfirmButton: boolean = true;
  @Input() showCancelButton: boolean = true;
  @Input() closeOnBackdrop: boolean = false;
  @Input() closeOnEscape: boolean = false;
  @Input() loading: boolean = false;
  @Input() icon: 'alert' | 'error' | 'success' | 'warning' | 'none' = 'alert';

  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  onBackdropClick(event: MouseEvent): void {
    if (this.closeOnBackdrop && event.target === event.currentTarget) {
      this.handleClose();
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.isOpen && this.closeOnEscape) {
      this.handleClose();
    }
  }

  handleClose(): void {
    this.close.emit();
  }

  handleConfirm(): void {
    this.confirm.emit();
  }

  handleCancel(): void {
    this.cancel.emit();
    this.handleClose();
  }

  get computedCancelText(): string {
    if (this.cancelText) return this.cancelText;
    return this.variant === 'confirm' ? 'No, cancel' : 'Cancel';
  }

  get sizeClasses(): string {
    switch (this.size) {
      case 'sm':
        return 'max-w-sm';
      case 'md':
        return 'max-w-md';
      case 'lg':
        return 'max-w-lg';
      case 'xl':
        return 'max-w-xl';
      case 'full':
        return 'max-w-4xl';
      default:
        return 'max-w-md';
    }
  }
}
