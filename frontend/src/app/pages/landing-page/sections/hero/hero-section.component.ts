import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonComponent } from '../../../../shared/components';
import {
  LucideShieldCheck,
  LucideUsers,
  LucideClock,
  LucideStar,
  LucidePlay,
} from '@lucide/angular';

@Component({
  selector: 'app-landing-hero',
  standalone: true,
  imports: [
    CommonModule,
    ButtonComponent,
    LucideShieldCheck,
    LucideUsers,
    LucideClock,
    LucideStar,
    LucidePlay,
  ],
  templateUrl: './hero-section.component.html',
  styleUrl: './hero-section.component.css',
})
export class LandingHeroComponent {
  stats = [
    {
      value: '15000+',
      label: 'Pengguna Aktif',
      icon: 'users',
    },
    {
      value: '24 Jam',
      label: 'Rata – rata Pencairan',
      icon: 'clock',
    },
    {
      value: '4.9/5',
      label: 'Rating Pengguna',
      icon: 'star',
    },
  ];
}
