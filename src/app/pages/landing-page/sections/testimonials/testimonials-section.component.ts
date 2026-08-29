import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

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
export class LandingTestimonialsComponent {
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
}
