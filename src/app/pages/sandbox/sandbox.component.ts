import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  ButtonComponent,
  BadgeComponent,
  InputComponent,
  InputNumberComponent,
  TextareaComponent,
  InputAmountComponent,
  CheckboxComponent,
  RadioComponent,
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  ToastService,
} from '../../shared/components';

@Component({
  selector: 'app-sandbox',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonComponent,
    BadgeComponent,
    InputComponent,
    InputNumberComponent,
    TextareaComponent,
    InputAmountComponent,
    CheckboxComponent,
    RadioComponent,
    DropdownComponent,
    ModalComponent,
  ],
  templateUrl: './sandbox.component.html',
  styleUrl: './sandbox.component.css',
})
export class SandboxComponent {
  readonly toastService = inject(ToastService);

  // Demo tag list untuk removable test
  tags: string[] = ['Angular', 'Tailwind', 'Saku Pay', 'Verified'];

  // Demo Form State untuk test ControlValueAccessor / Two-Way Binding
  demoText: string = 'PT Saku Digital Nusantara';
  demoSearch: string = '';
  demoNumber: string = '45';
  demoEmail: string = 'admin@saku.id';
  demoAmount: number = 2500000;
  demoTextarea: string =
    'Platform digital terpercaya untuk pengelolaan transaksi finansial cepat, efisien, dan aman.';

  // Plafond & Loan Setting Demo State
  minScore: number = 999;
  maxScore: number = 999;
  minPlafond: number | null = null;
  maxPlafond: number | null = null;
  bunga: number = 999;
  biayaAdmin: number | null = null;

  demoCheckbox1: boolean = true;
  demoCheckbox2: boolean = false;
  demoCheckboxIndeterminate: boolean = true;

  demoRole: string = 'superadmin';
  demoBranch: string = '';

  readonly roleOptions: DropdownOption[] = [
    { value: 'superadmin', label: 'Superadmin' },
    { value: 'marketing', label: 'Marketing' },
    { value: 'bm', label: 'Branch Manager (BM)' },
    { value: 'backoffice', label: 'Back Office' },
  ];

  readonly branchOptions: DropdownOption[] = [
    { value: 'JKT-01', label: 'Jakarta Pusat (Kantor Pusat)' },
    { value: 'BDG-01', label: 'Bandung - Dago' },
    { value: 'SBY-01', label: 'Surabaya - Gubeng' },
    { value: 'MDN-01', label: 'Medan - Merdeka' },
    { value: 'DPS-01', label: 'Denpasar - Sunset Road' },
  ];

  readonly cityOptions: DropdownOption[] = [
    { value: 'jakarta', label: 'Jakarta' },
    { value: 'bandung', label: 'Bandung' },
    { value: 'surabaya', label: 'Surabaya' },
    { value: 'medan', label: 'Medan' },
    { value: 'denpasar', label: 'Denpasar' },
  ];

  readonly statusFilterOptions: DropdownOption[] = [
    { value: 'success', label: 'Berhasil (Success)' },
    { value: 'pending', label: 'Menunggu (Pending)' },
    { value: 'failed', label: 'Gagal (Failed)' },
  ];

  selectedStatusFilter: string = '';

  // Modal State
  isFormModalOpen: boolean = false;
  isConfirmModalOpen: boolean = false;

  // Form Modal State (Gambar 1)
  modalCabangNama: string = '';
  modalCabangKota: string = '';
  modalCabangDefault: string = 'Ya';
  modalCabangStatus: string = 'Aktif';

  openFormModal(): void {
    this.isFormModalOpen = true;
  }

  closeFormModal(): void {
    this.isFormModalOpen = false;
  }

  submitFormModal(): void {
    this.isFormModalOpen = false;
    this.toastService.success('Cabang baru berhasil ditambahkan.');
  }

  openConfirmModal(): void {
    this.isConfirmModalOpen = true;
  }

  closeConfirmModal(): void {
    this.isConfirmModalOpen = false;
  }

  submitConfirmModal(): void {
    this.isConfirmModalOpen = false;
    this.toastService.error('Item has been deleted.');
  }

  // Trigger Toasts (Gambar 3)
  showSuccessToast(): void {
    this.toastService.success('Item moved successfully.');
  }

  showErrorToast(): void {
    this.toastService.error('Item has been deleted.');
  }

  showWarningToast(): void {
    this.toastService.warning('Improve password difficulty.');
  }

  removeTag(tagToRemove: string): void {
    this.tags = this.tags.filter((tag) => tag !== tagToRemove);
  }

  resetTags(): void {
    this.tags = ['Angular', 'Tailwind', 'Saku Pay', 'Verified'];
  }
}
