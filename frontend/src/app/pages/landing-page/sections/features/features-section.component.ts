import {
  Component,
  OnInit,
  OnDestroy,
  Inject,
  PLATFORM_ID,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import {
  LucideHandCoins,
  LucideRotateCcw,
  LucideWallet,
} from '@lucide/angular';

export interface FeatureCard {
  id: number;
  icon: 'hand-coins' | 'rotate-ccw-clock' | 'wallet';
  title: string;
  description: string;
  highlight?: boolean;
  cardBgClass?: string;
  image: string;
}

@Component({
  selector: 'app-landing-features',
  standalone: true,
  imports: [
    CommonModule,
    LucideHandCoins,
    LucideRotateCcw,
    LucideWallet,
  ],
  templateUrl: './features-section.component.html',
  styleUrl: './features-section.component.css',
})
export class LandingFeaturesComponent implements OnInit, OnDestroy {
  currentSlide = 0;
  private autoPlayTimer: any;
  private touchStartX = 0;
  private touchEndX = 0;
  private isBrowser: boolean;

  features: FeatureCard[] = [
    {
      id: 1,
      icon: 'hand-coins',
      title: 'Limit yang Disesuaikan, Bukan Angka Standar Semua Orang',
      description: 'Kebutuhanmu beda, jadi limitnya juga harus ngikutin rencana dan kemampuanmu sendiri.',
      highlight: false,
      cardBgClass: 'bg-[#FDF8EF] border border-[#F0E6D2]',
      image: '/landing/phone-pinjaman.png',
    },
    {
      id: 2,
      icon: 'rotate-ccw-clock',
      title: 'Ajukan Hari Ini, Dana Cair Sebelum Sempat Khawatir',
      description: 'Siap pakai kapan saja hanya dalam 24 jam setelah verifikasi selesai.',
      highlight: true,
      cardBgClass: 'bg-gradient-to-b from-[#FF792E] to-[#FF6207]',
      image: '/landing/phone-pinjaman-2.png',
    },
    {
      id: 3,
      icon: 'wallet',
      title: 'Cicilan Diatur Biar Tetap Nyaman di Kantongmu',
      description: 'Fleksibilitas tenor dan cicilan bulanan yang paling pas sama skema keuanganmu.',
      highlight: false,
      cardBgClass: 'bg-primary-10 border border-primary-20',
      image: '/landing/phone-pinjaman-3.png',
    },
  ];

  constructor(
    @Inject(PLATFORM_ID) platformId: Object,
    private cdr: ChangeDetectorRef
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      this.startAutoPlay();
    }
  }

  ngOnDestroy(): void {
    this.stopAutoPlay();
  }

  startAutoPlay(): void {
    this.stopAutoPlay();
    if (!this.isBrowser) return;
    this.autoPlayTimer = setInterval(() => {
      this.nextSlide();
    }, 3000);
  }

  stopAutoPlay(): void {
    if (this.autoPlayTimer) {
      clearInterval(this.autoPlayTimer);
      this.autoPlayTimer = null;
    }
  }

  nextSlide(): void {
    this.currentSlide = (this.currentSlide + 1) % this.features.length;
    this.cdr.detectChanges();
  }

  prevSlide(): void {
    this.currentSlide =
      (this.currentSlide - 1 + this.features.length) % this.features.length;
    this.cdr.detectChanges();
  }

  goToSlide(index: number): void {
    this.currentSlide = index;
    this.cdr.detectChanges();
    this.startAutoPlay();
  }

  onTouchStart(event: TouchEvent): void {
    this.touchStartX = event.changedTouches[0].screenX;
    this.stopAutoPlay();
  }

  onTouchEnd(event: TouchEvent): void {
    this.touchEndX = event.changedTouches[0].screenX;
    this.handleSwipe();
    this.startAutoPlay();
  }

  private handleSwipe(): void {
    const swipeThreshold = 40;
    const diff = this.touchStartX - this.touchEndX;
    if (diff > swipeThreshold) {
      this.nextSlide();
    } else if (diff < -swipeThreshold) {
      this.prevSlide();
    }
  }
}
