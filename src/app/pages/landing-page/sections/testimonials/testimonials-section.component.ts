import {
  Component,
  OnInit,
  OnDestroy,
  Inject,
  PLATFORM_ID,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';

export interface Testimonial {
  id: number;
  quote: string;
  name: string;
  role: string;
  city: string;
  initials: string;
  tag: string;
}

@Component({
  selector: 'app-landing-testimonials',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './testimonials-section.component.html',
  styleUrl: './testimonials-section.component.css',
})
export class LandingTestimonialsComponent implements OnInit, OnDestroy {
  currentSlide = 0;
  private autoPlayTimer: any;
  private touchStartX = 0;
  private touchEndX = 0;
  private isBrowser: boolean;

  testimonials: Testimonial[] = [
    {
      id: 1,
      quote:
        'Awalnya mikir harus nunggu tabungan bertahun-tahun buat nikah. Tapi pas pake SAKU semuanya jadi lebih cepat terealisasi, prosesnya juga gak ribet!',
      name: 'Rifky',
      role: 'Wiraswasta',
      city: 'Jakarta',
      initials: 'R',
      tag: '#Pernikahan',
    },
    {
      id: 2,
      quote:
        'Rumah orang tua udah lama pengen direnovasi tapi selalu terkendala dana. Lewat SAKU, akhirnya bisa jalan juga, dan cicilannya masuk akal buat gaji pokok.',
      name: 'Erwin',
      role: 'Guru',
      city: 'Surabaya',
      initials: 'E',
      tag: '#RenovasiRumah',
    },
    {
      id: 3,
      quote:
        'Pas ada kesempatan buat sertifikasi mendadak, sempat ragu karena dananya belum siap. Untung proses di SAKU cepet banget, jadi karier gak tertunda.',
      name: 'Sari',
      role: 'Freelancer',
      city: 'Bandung',
      initials: 'S',
      tag: '#Karier & Skill',
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
    this.currentSlide = (this.currentSlide + 1) % this.testimonials.length;
    this.cdr.detectChanges();
  }

  prevSlide(): void {
    this.currentSlide =
      (this.currentSlide - 1 + this.testimonials.length) %
      this.testimonials.length;
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
