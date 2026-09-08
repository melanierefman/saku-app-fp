import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucidePercent,
  LucideGlobe,
  LucideShieldCheck,
  LucideHeadphones,
} from '@lucide/angular';

export interface BenefitItem {
  id: number;
  icon: string;
  title: string;
}

@Component({
  selector: 'app-landing-benefits',
  standalone: true,
  imports: [
    CommonModule,
    LucidePercent,
    LucideGlobe,
    LucideShieldCheck,
    LucideHeadphones,
  ],
  templateUrl: './benefits-section.component.html',
  styleUrl: './benefits-section.component.css',
})
export class LandingBenefitsComponent {
  benefits: BenefitItem[] = [
    {
      id: 1,
      icon: 'percent',
      title: 'Bunga Jelas dari Awal, Gak Ada Kejutan',
    },
    {
      id: 2,
      icon: 'globe',
      title: 'Proses 100% Online, Dari Rumah Aja',
    },
    {
      id: 3,
      icon: 'shield-check',
      title: 'Diawasi Resmi, Datamu Terjamin Aman',
    },
    {
      id: 4,
      icon: 'headset',
      title: 'Ada yang Siap Bantu, Kapan pun Kamu Butuh',
    },
  ];
}
