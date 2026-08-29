import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

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
  imports: [CommonModule],
  templateUrl: './features-section.component.html',
  styleUrl: './features-section.component.css',
})
export class LandingFeaturesComponent {
  features: FeatureCard[] = [
    {
      id: 1,
      icon: 'hand-coins',
      title: 'Limit yang Disesuaikan, Bukan Angka Standar Semua Orang',
      description: 'Limit disesuaikan dengan profil dan kebutuhanmu, gak cuma satu ukuran untuk semua orang.',
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
}
