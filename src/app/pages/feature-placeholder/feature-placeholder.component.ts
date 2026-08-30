import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { AuthStore } from '../../core/store/auth.store';

import { LucideAppWindow, LucideCircleAlert } from '@lucide/angular';

@Component({
  selector: 'app-feature-placeholder',
  standalone: true,
  imports: [CommonModule, LucideAppWindow, LucideCircleAlert],
  template: `
    <div class="space-y-6">
      <div class="bg-white rounded-xl p-6 sm:p-8 border border-[#E5E7EB] text-left">
        <div class="flex items-center gap-3 mb-2">
          <span class="p-2.5 rounded-2xl bg-primary/10 text-primary">
            <svg lucideAppWindow [size]="24" class="w-6 h-6"></svg>
          </span>
          <div>
            <h1 class="text-xl sm:text-2xl font-bold text-neutral-90 tracking-tight">
              {{ title }}
            </h1>
            <p class="text-xs sm:text-sm text-neutral-40">
              Menu Portal SAKU &bull; {{ userRoleDisplay }}
            </p>
          </div>
        </div>

        <div class="mt-6 p-8 rounded-2xl bg-[#FFF9F5] border border-[#FFE7D6] flex flex-col items-center justify-center text-center space-y-3">
          <div class="w-14 h-14 rounded-full bg-primary/10 flex items-center justify-center text-primary">
            <svg lucideCircleAlert [size]="28" class="w-7 h-7"></svg>
          </div>
          <h2 class="text-lg font-bold text-neutral-90">Selamat Datang di Halaman {{ title }}</h2>
          <p class="text-sm text-neutral-50 max-w-md">
            Halaman ini sedang dalam tahap pengembangan. Fitur lengkap untuk {{ title }} akan segera tersedia.
          </p>
        </div>
      </div>
    </div>
  `,
})
export class FeaturePlaceholderComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private authStore = inject(AuthStore);

  title: string = 'Halaman';

  get userRoleDisplay(): string {
    return (this.authStore.userRole() || 'KARYAWAN').replace(/_/g, ' ').toUpperCase();
  }

  ngOnInit(): void {
    this.route.data.subscribe((data) => {
      if (data['title']) {
        this.title = data['title'];
      }
    });
  }
}
