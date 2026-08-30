import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideMail, LucidePhone, LucideCalendar } from '@lucide/angular';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, LucideMail, LucidePhone, LucideCalendar],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css',
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
}
