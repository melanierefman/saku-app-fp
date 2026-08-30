import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucideCheck,
  LucideX,
  LucideCircleAlert,
  LucideInfo,
} from '@lucide/angular';
import { Toast, ToastService } from './toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [
    CommonModule,
    LucideCheck,
    LucideX,
    LucideCircleAlert,
    LucideInfo,
  ],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css',
})
export class ToastContainerComponent {
  private readonly toastService = inject(ToastService);
  readonly toasts = this.toastService.toasts;

  dismiss(toast: Toast): void {
    this.toastService.remove(toast.id);
  }
}
