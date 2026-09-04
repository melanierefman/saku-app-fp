import {
  Component,
  OnInit,
  inject,
  signal,
  computed,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  ButtonComponent,
  InputComponent,
  InputNumberComponent,
  InputAmountComponent,
  RadioComponent,
  ModalComponent,
  ToastService,
} from '../../../../shared/components';
import { Plafond, PlafondRequest, PlafondService } from '../../../../core';
import { LucideCircleAlert, LucideArrowLeft } from '@lucide/angular';

@Component({
  selector: 'app-plafond-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ButtonComponent,
    InputComponent,
    InputNumberComponent,
    InputAmountComponent,
    RadioComponent,
    ModalComponent,
    LucideCircleAlert,
    LucideArrowLeft,
  ],
  templateUrl: './plafond-form.component.html',
  styleUrl: './plafond-form.component.css',
})
export class PlafondFormComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private plafondService = inject(PlafondService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  isEditMode = signal<boolean>(false);
  plafondId: string = '';
  isLoading = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  isConfirmModalOpen = signal<boolean>(false);
  isDeleteModalOpen = signal<boolean>(false);

  // Form States
  nama: string = '';
  minSkor: number | null = null;
  maxSkor: number | null = null;
  minPendapatan: number | null = null;
  minPlafond: number | null = null;
  maxPlafond: number | null = null;
  bunga: number | null = null;
  biayaAdmin: number | null = null;
  status: boolean = true;

  // Validation Error States
  namaError: string = '';
  minSkorError: string = '';
  maxSkorError: string = '';
  minPendapatanError: string = '';
  minPlafondError: string = '';
  maxPlafondError: string = '';
  bungaError: string = '';
  biayaAdminError: string = '';
  statusError: string = '';

  pageTitle = computed<string>(() =>
    this.isEditMode() ? 'Edit Plafond' : 'Tambah Plafond'
  );

  ngOnInit(): void {
    this.plafondId = this.route.snapshot.paramMap.get('id') || '';
    if (this.plafondId) {
      this.isEditMode.set(true);
      this.fetchPlafondDetail(this.plafondId);
    }
  }

  fetchPlafondDetail(id: string): void {
    this.isLoading.set(true);
    this.plafondService.getById(id).subscribe({
      next: (plafond) => {
        if (plafond && (plafond.nama || plafond.id)) {
          this.populateFields(plafond);
          this.isLoading.set(false);
          this.cdr.detectChanges();
        } else {
          this.fetchFromListFallback(id);
        }
      },
      error: () => {
        this.fetchFromListFallback(id);
      },
    });
  }

  private fetchFromListFallback(id: string): void {
    this.plafondService.getAll().subscribe({
      next: (list) => {
        const found = (list || []).find((p) => p.id === id);
        if (found) {
          this.populateFields(found);
        } else {
          this.toastService.error('Data plafond tidak ditemukan.');
        }
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch plafond:', err);
        this.toastService.error('Gagal memuat data plafond.');
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
    });
  }

  private populateFields(plafond: Plafond): void {
    this.nama = plafond.nama || '';
    this.minSkor = plafond.minSkor ?? null;
    this.maxSkor = plafond.maxSkor ?? null;
    this.minPendapatan = plafond.minPendapatan ?? null;
    this.minPlafond = plafond.minPlafond ?? null;
    this.maxPlafond = plafond.maxPlafond ?? plafond.plafondMaksimal ?? null;

    // If bunga is decimal e.g. 0.03 -> convert to 3% for UI display
    if (plafond.bunga !== null && plafond.bunga !== undefined) {
      this.bunga = plafond.bunga <= 1 ? parseFloat((plafond.bunga * 100).toFixed(2)) : plafond.bunga;
    } else {
      this.bunga = null;
    }

    this.biayaAdmin = plafond.biayaAdmin ?? null;
    this.status =
      plafond.status === true || String(plafond.status) === 'true';

    this.cdr.detectChanges();
  }

  onStatusChange(): void {
    if (this.statusError) {
      this.statusError = '';
    }
  }

  validate(): boolean {
    let isValid = true;
    this.namaError = '';
    this.minSkorError = '';
    this.maxSkorError = '';
    this.minPendapatanError = '';
    this.minPlafondError = '';
    this.maxPlafondError = '';
    this.bungaError = '';
    this.biayaAdminError = '';
    this.statusError = '';

    // 1. Nama
    if (!this.nama.trim()) {
      this.namaError = 'Nama plafond wajib diisi';
      isValid = false;
    } else if (this.nama.trim().length < 3) {
      this.namaError = 'Nama plafond minimal 3 karakter';
      isValid = false;
    }

    // 2. Minimal Skor
    if (this.minSkor === null || this.minSkor === undefined) {
      this.minSkorError = 'Minimal skor wajib diisi';
      isValid = false;
    } else if (this.minSkor < 0) {
      this.minSkorError = 'Minimal skor tidak boleh bernilai negatif';
      isValid = false;
    }

    // 3. Maksimal Skor
    if (this.maxSkor === null || this.maxSkor === undefined) {
      this.maxSkorError = 'Maksimal skor wajib diisi';
      isValid = false;
    } else if (this.minSkor !== null && this.maxSkor < this.minSkor) {
      this.maxSkorError = 'Maksimal skor tidak boleh lebih kecil dari minimal skor';
      isValid = false;
    }

    // 4. Minimal Pendapatan
    if (this.minPendapatan === null || this.minPendapatan === undefined || this.minPendapatan <= 0) {
      this.minPendapatanError = 'Minimal pendapatan wajib diisi dan lebih dari 0';
      isValid = false;
    }

    // 5. Minimal Plafond
    if (this.minPlafond === null || this.minPlafond === undefined || this.minPlafond <= 0) {
      this.minPlafondError = 'Minimal plafond wajib diisi dan lebih dari 0';
      isValid = false;
    }

    // 6. Maksimal Plafond
    if (this.maxPlafond === null || this.maxPlafond === undefined || this.maxPlafond <= 0) {
      this.maxPlafondError = 'Maksimal plafond wajib diisi dan lebih dari 0';
      isValid = false;
    } else if (this.minPlafond !== null && this.maxPlafond < this.minPlafond) {
      this.maxPlafondError = 'Maksimal plafond tidak boleh lebih kecil dari minimal plafond';
      isValid = false;
    }

    // 7. Bunga (%)
    if (this.bunga === null || this.bunga === undefined || this.bunga < 0) {
      this.bungaError = 'Bunga wajib diisi';
      isValid = false;
    }

    // 8. Biaya Admin
    if (this.biayaAdmin === null || this.biayaAdmin === undefined || this.biayaAdmin < 0) {
      this.biayaAdminError = 'Biaya admin wajib diisi';
      isValid = false;
    }

    // 9. Status
    if (this.status === null || this.status === undefined || typeof this.status !== 'boolean') {
      this.statusError = 'Status plafond wajib dipilih';
      isValid = false;
    }

    return isValid;
  }

  onSubmit(): void {
    if (!this.validate()) return;
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal(): void {
    if (this.isSubmitting()) return;
    this.isConfirmModalOpen.set(false);
  }

  confirmSubmit(): void {
    this.isConfirmModalOpen.set(false);
    this.isSubmitting.set(true);

    const statusBool = this.status === true || String(this.status) === 'true';
    // Normalize bunga to decimal (e.g. 3% -> 0.03) for backend
    const bungaDecimal = (this.bunga || 0) > 1 ? (this.bunga || 0) / 100 : (this.bunga || 0);

    const payload: PlafondRequest = {
      nama: this.nama.trim(),
      minSkor: Number(this.minSkor) || 0,
      maxSkor: Number(this.maxSkor) || 0,
      minPendapatan: Number(this.minPendapatan) || 0,
      minPlafond: Number(this.minPlafond) || 0,
      maxPlafond: Number(this.maxPlafond) || 0,
      plafondMaksimal: Number(this.maxPlafond) || 0,
      bunga: bungaDecimal,
      biayaAdmin: Number(this.biayaAdmin) || 0,
      status: statusBool,
    };

    if (this.isEditMode()) {
      this.plafondService.updateById(this.plafondId, payload).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.toastService.success('Data plafond berhasil diperbarui!');
          this.router.navigate(['/master/plafond']);
        },
        error: (err) => {
          this.isSubmitting.set(false);
          console.error('Failed to update plafond:', err);
          this.toastService.error(
            err?.error?.message || 'Gagal memperbarui data plafond.'
          );
        },
      });
    } else {
      this.plafondService.create(payload).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.toastService.success('Plafond baru berhasil ditambahkan!');
          this.router.navigate(['/master/plafond']);
        },
        error: (err) => {
          this.isSubmitting.set(false);
          console.error('Failed to create plafond:', err);
          this.toastService.error(
            err?.error?.message || 'Gagal menambahkan plafond baru.'
          );
        },
      });
    }
  }

  openDeleteModal(): void {
    this.isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    if (this.isSubmitting()) return;
    this.isDeleteModalOpen.set(false);
  }

  confirmDelete(): void {
    this.isDeleteModalOpen.set(false);
    this.isSubmitting.set(true);

    this.plafondService.delete(this.plafondId).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.toastService.success('Data plafond berhasil dihapus.');
        this.router.navigate(['/master/plafond']);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        console.error('Failed to delete plafond:', err);
        this.toastService.error(
          err?.error?.message || 'Gagal menghapus data plafond.'
        );
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/master/plafond']);
  }
}
