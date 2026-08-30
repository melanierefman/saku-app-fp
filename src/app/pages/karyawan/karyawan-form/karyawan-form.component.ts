import {
  Component,
  OnInit,
  inject,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  BreadcrumbsComponent,
  BreadcrumbItem,
  ButtonComponent,
  InputComponent,
  DropdownComponent,
  DropdownOption,
  RadioComponent,
  ModalComponent,
  ToastService,
} from '../../../shared/components';
import {
  Karyawan,
  KaryawanCreateRequest,
  KaryawanUpdateRequest,
  KaryawanService,
  Role,
  RoleService,
  Cabang,
  CabangService,
  formatRoleName,
} from '../../../core';
import { LucideEye, LucideEyeOff } from '@lucide/angular';

@Component({
  selector: 'app-karyawan-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    BreadcrumbsComponent,
    ButtonComponent,
    InputComponent,
    DropdownComponent,
    RadioComponent,
    ModalComponent,
    LucideEye,
    LucideEyeOff,
  ],
  templateUrl: './karyawan-form.component.html',
  styleUrl: './karyawan-form.component.css',
})
export class KaryawanFormComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private karyawanService = inject(KaryawanService);
  private roleService = inject(RoleService);
  private cabangService = inject(CabangService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  isEditMode: boolean = false;
  karyawanId: string = '';
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  isConfirmModalOpen: boolean = false;
  isDeleteModalOpen: boolean = false;

  // Form State
  nama: string = '';
  username: string = '';
  email: string = '';
  password: string = '';
  showPassword: boolean = false;
  mstBranchId: string = '';
  mstRoleId: string = '';
  status: string = 'true';

  // Validation Error States
  namaError: string = '';
  usernameError: string = '';
  emailError: string = '';
  passwordError: string = '';
  branchError: string = '';
  roleError: string = '';

  // Dropdown options & raw lists
  roles: Role[] = [];
  branches: Cabang[] = [];
  roleOptions: DropdownOption[] = [];
  branchOptions: DropdownOption[] = [];

  get breadcrumbs(): BreadcrumbItem[] {
    return [
      { label: 'Dashboard', url: '/dashboard' },
      { label: 'Daftar Karyawan', url: '/master/karyawan' },
      {
        label: this.isEditMode ? 'Edit Karyawan' : 'Tambah Karyawan',
        active: true,
      },
    ];
  }

  get pageTitle(): string {
    return this.isEditMode ? 'Edit Karyawan' : 'Tambah Karyawan';
  }

  ngOnInit(): void {
    this.loadRoles();
    this.loadBranches();

    // Check if editing
    this.karyawanId = this.route.snapshot.paramMap.get('id') || '';
    if (this.karyawanId) {
      this.isEditMode = true;
      this.fetchKaryawanDetail(this.karyawanId);
    }
  }

  loadRoles(): void {
    this.roleService.getAll().subscribe({
      next: (roles) => {
        this.roles = roles || [];
        this.roleOptions = this.roles.map((r) => ({
          value: r.id,
          label: formatRoleName(r.nama),
        }));
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load roles:', err);
      },
    });
  }

  loadBranches(): void {
    this.cabangService.getAll().subscribe({
      next: (branches) => {
        this.branches = branches || [];
        this.branchOptions = this.branches.map((b) => ({
          value: b.id,
          label: b.nama,
        }));
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load branches:', err);
      },
    });
  }

  fetchKaryawanDetail(id: string): void {
    this.isLoading = true;

    // First try GET /api/karyawan/{id}
    this.karyawanService.getById(id).subscribe({
      next: (karyawan) => {
        if (karyawan && (karyawan.nama || karyawan.id)) {
          this.populateFields(karyawan);
          this.isLoading = false;
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
    this.karyawanService.getAll().subscribe({
      next: (list) => {
        const found = (list || []).find((k) => k.id === id);
        if (found) {
          this.populateFields(found);
        } else {
          this.toastService.error('Data karyawan tidak ditemukan.');
        }
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch karyawan from list:', err);
        this.toastService.error('Gagal memuat data karyawan.');
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  private populateFields(karyawan: Karyawan | any): void {
    this.nama = karyawan.nama || karyawan.name || '';
    this.username = karyawan.username || '';
    this.email = karyawan.email || '';
    this.status =
      karyawan.status === true || String(karyawan.status) === 'true'
        ? 'true'
        : 'false';

    // Resolve Role ID
    if (karyawan.mstRoleId) {
      this.mstRoleId = karyawan.mstRoleId;
    } else if (karyawan.roleId) {
      this.mstRoleId = karyawan.roleId;
    } else if (karyawan.roleNama) {
      const match = this.roleOptions.find(
        (r) => r.label.toLowerCase() === karyawan.roleNama.toLowerCase()
      );
      if (match) this.mstRoleId = match.value;
    }

    // Resolve Branch ID
    if (karyawan.mstBranchId) {
      this.mstBranchId = karyawan.mstBranchId;
    } else if (karyawan.branchId) {
      this.mstBranchId = karyawan.branchId;
    } else if (karyawan.cabangNama) {
      const match = this.branchOptions.find(
        (b) => b.label.toLowerCase() === karyawan.cabangNama.toLowerCase()
      );
      if (match) this.mstBranchId = match.value;
    }

    this.cdr.detectChanges();
  }

  validate(): boolean {
    let isValid = true;
    this.namaError = '';
    this.usernameError = '';
    this.emailError = '';
    this.passwordError = '';
    this.branchError = '';
    this.roleError = '';

    if (!this.nama.trim()) {
      this.namaError = 'Nama karyawan wajib diisi';
      isValid = false;
    }

    if (!this.username.trim()) {
      this.usernameError = 'Username karyawan wajib diisi';
      isValid = false;
    }

    if (!this.email.trim()) {
      this.emailError = 'Email karyawan wajib diisi';
      isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email.trim())) {
      this.emailError = 'Format email tidak valid';
      isValid = false;
    }

    if (!this.password.trim()) {
      this.passwordError = 'Password wajib diisi';
      isValid = false;
    }

    if (!this.mstBranchId) {
      this.branchError = 'Cabang wajib dipilih';
      isValid = false;
    }

    if (!this.mstRoleId) {
      this.roleError = 'Role wajib dipilih';
      isValid = false;
    }

    return isValid;
  }

  onSubmit(): void {
    if (!this.validate()) return;
    this.isConfirmModalOpen = true;
  }

  confirmSubmit(): void {
    this.isConfirmModalOpen = false;
    this.isSubmitting = true;

    if (this.isEditMode) {
      // Update (PUT sends all fields)
      const payload: KaryawanUpdateRequest = {
        id: this.karyawanId,
        nama: this.nama.trim(),
        username: this.username.trim(),
        email: this.email.trim(),
        password: this.password.trim(),
        status: this.status === 'true',
        mstRoleId: this.mstRoleId,
        mstBranchId: this.mstBranchId,
      };

      this.karyawanService.update(payload).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.toastService.success('Data karyawan berhasil diperbarui!');
          this.router.navigate(['/master/karyawan']);
        },
        error: (err) => {
          this.isSubmitting = false;
          console.error('Failed to update karyawan:', err);
          this.toastService.error(
            err?.error?.message || 'Gagal memperbarui data karyawan.'
          );
        },
      });
    } else {
      // Create
      const payload: KaryawanCreateRequest = {
        nama: this.nama.trim(),
        username: this.username.trim(),
        email: this.email.trim(),
        password: this.password,
        status: this.status === 'true',
        mstRoleId: this.mstRoleId,
        mstBranchId: this.mstBranchId,
      };

      this.karyawanService.create(payload).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.toastService.success('Karyawan baru berhasil ditambahkan!');
          this.router.navigate(['/master/karyawan']);
        },
        error: (err) => {
          this.isSubmitting = false;
          console.error('Failed to create karyawan:', err);
          this.toastService.error(
            err?.error?.message || 'Gagal menambahkan karyawan baru.'
          );
        },
      });
    }
  }

  openDeleteModal(): void {
    this.isDeleteModalOpen = true;
  }

  closeDeleteModal(): void {
    this.isDeleteModalOpen = false;
  }

  confirmDelete(): void {
    this.isDeleteModalOpen = false;
    this.isSubmitting = true;

    this.karyawanService.delete(this.karyawanId).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.toastService.success('Data karyawan berhasil dihapus.');
        this.router.navigate(['/master/karyawan']);
      },
      error: (err) => {
        this.isSubmitting = false;
        console.error('Failed to delete karyawan:', err);
        this.toastService.error(
          err?.error?.message || 'Gagal menghapus data karyawan.'
        );
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/master/karyawan']);
  }
}
