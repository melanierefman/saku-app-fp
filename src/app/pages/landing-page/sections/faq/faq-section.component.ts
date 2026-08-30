import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideChevronDown } from '@lucide/angular';

export interface FaqItem {
  id: number;
  question: string;
  answer: string;
  isOpen: boolean;
}

@Component({
  selector: 'app-landing-faq',
  standalone: true,
  imports: [CommonModule, LucideChevronDown],
  templateUrl: './faq-section.component.html',
  styleUrl: './faq-section.component.css',
})
export class LandingFaqComponent {
  faqs: FaqItem[] = [
    {
      id: 1,
      question: 'Berapa limit pinjaman maksimal di SAKU?',
      answer:
        'Limit disesuaikan dengan profil dan kebutuhanmu, mulai dari beberapa juta hingga ratusan juta rupiah, tergantung hasil verifikasi.',
      isOpen: true,
    },
    {
      id: 2,
      question: 'Berapa lama proses pencairan dana?',
      answer:
        'Setelah pengajuan disetujui, dana bisa cair dalam waktu 24 jam langsung ke rekeningmu.',
      isOpen: false,
    },
    {
      id: 3,
      question: 'Apa saja syarat pengajuan pinjaman di SAKU?',
      answer:
        'WNI berusia minimal 21 tahun, memiliki KTP elektronik yang valid, dan memiliki nomor rekening bank aktif atas nama pribadi.',
      isOpen: false,
    },
    {
      id: 4,
      question: 'Bagaimana cara menghitung bunga pinjaman?',
      answer:
        'Bunga dihitung secara transparan dan flat per bulan sesuai simulasi yang ditampilkan sebelum Anda menyetujui kontrak pinjaman.',
      isOpen: false,
    },
    {
      id: 5,
      question: 'Apakah ada biaya tersembunyi selain bunga?',
      answer:
        'Tidak ada biaya tersembunyi. Semua rincian biaya admin dan provisi dijelaskan secara transparan di awal sebelum pengajuan diproses.',
      isOpen: false,
    },
  ];

  toggleFaq(id: number): void {
    this.faqs = this.faqs.map((faq) => {
      if (faq.id === id) {
        return { ...faq, isOpen: !faq.isOpen };
      }
      return faq;
    });
  }
}
