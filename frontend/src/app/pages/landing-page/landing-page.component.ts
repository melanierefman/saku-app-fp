import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Title, Meta } from '@angular/platform-browser';
import { NavbarComponent, FooterComponent } from '../../shared/components';
import { LandingHeroComponent } from './sections/hero/hero-section.component';
import { LandingFeaturesComponent } from './sections/features/features-section.component';
import { LandingBenefitsComponent } from './sections/benefits/benefits-section.component';
import { LandingTestimonialsComponent } from './sections/testimonials/testimonials-section.component';
import { LandingFaqComponent } from './sections/faq/faq-section.component';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    LandingHeroComponent,
    LandingFeaturesComponent,
    LandingBenefitsComponent,
    LandingTestimonialsComponent,
    LandingFaqComponent,
    FooterComponent,
  ],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css',
})
export class LandingPageComponent implements OnInit {
  constructor(
    private titleService: Title,
    private metaService: Meta,
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle('SAKU - Solusi Pinjaman Digital Cepat, Aman & Terpercaya | Aplikasi Fintech SAKU');
    this.metaService.updateTag({
      name: 'description',
      content: 'SAKU adalah platform pinjaman digital terdepan di Indonesia. Dapatkan pinjaman dana tunai dan modal usaha kilat hingga Rp 50 Juta dengan bunga rendah mulai 0.99%, persetujuan 5 menit, tanpa agunan, aman dan terpercaya sesuai regulasi yang berlaku.',
    });
    this.metaService.updateTag({
      name: 'keywords',
      content: 'SAKU, aplikasi SAKU, SAKU app, pinjaman SAKU, pinjaman online SAKU, fintech SAKU, pinjaman dana tunai, pinjaman terpercaya, pinjaman bunga rendah, kredit instan, pinjaman modal UMKM',
    });
    this.metaService.updateTag({ property: 'og:title', content: 'SAKU - Solusi Pinjaman Digital Cepat, Aman & Terpercaya' });
    this.metaService.updateTag({
      property: 'og:description',
      content: 'Solusi pinjaman dana tunai & modal usaha instan hingga Rp 50 Juta dengan bunga bersahabat mulai 0.99%. Aman dan sesuai regulasi yang berlaku.',
    });
  }
}

