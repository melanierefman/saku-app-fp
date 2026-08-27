import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonComponent, BadgeComponent } from '../../shared/components';

@Component({
  selector: 'app-sandbox',
  standalone: true,
  imports: [CommonModule, ButtonComponent, BadgeComponent],
  templateUrl: './sandbox.component.html',
  styleUrl: './sandbox.component.css',
})
export class SandboxComponent {
  // Demo tag list untuk removable test
  tags: string[] = ['Angular', 'Tailwind', 'Saku Pay', 'Verified'];

  removeTag(tagToRemove: string): void {
    this.tags = this.tags.filter((tag) => tag !== tagToRemove);
  }

  resetTags(): void {
    this.tags = ['Angular', 'Tailwind', 'Saku Pay', 'Verified'];
  }
}
