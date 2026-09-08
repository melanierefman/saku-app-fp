import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, ButtonComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  isMobileMenuOpen: boolean = false;
  isScrolled: boolean = false;

  navLinks = [
    { label: 'Beranda', href: '#hero', active: true },
    { label: 'Fitur', href: '#fitur', active: false },
    { label: 'Tentang SAKU', href: '#tentang', active: false },
    { label: 'Kata Mereka', href: '#testimoni', active: false },
    { label: 'FAQ', href: '#faq', active: false },
  ];

  @HostListener('window:scroll', [])
  onWindowScroll(): void {
    this.isScrolled = window.scrollY > 20;

    const scrollPosition = window.scrollY + 140;
    for (const link of this.navLinks) {
      const section = document.querySelector(link.href) as HTMLElement;
      if (section) {
        const top = section.offsetTop;
        const height = section.offsetHeight;
        if (scrollPosition >= top && scrollPosition < top + height) {
          this.navLinks.forEach((l) => (l.active = l.href === link.href));
        }
      }
    }
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

  scrollToSection(href: string, event: Event): void {
    event.preventDefault();
    this.closeMobileMenu();
    this.navLinks.forEach((link) => {
      link.active = link.href === href;
    });
    const element = document.querySelector(href);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }
}
