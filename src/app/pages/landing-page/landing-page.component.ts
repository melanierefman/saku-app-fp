import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
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
export class LandingPageComponent {}
