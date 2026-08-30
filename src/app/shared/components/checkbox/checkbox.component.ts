import {
  Component,
  EventEmitter,
  Input,
  Output,
  forwardRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ControlValueAccessor,
  FormsModule,
  NG_VALUE_ACCESSOR,
} from '@angular/forms';

import { LucideCheck, LucideMinus } from '@lucide/angular';

let nextUniqueCheckboxId = 0;

@Component({
  selector: 'app-checkbox',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideCheck, LucideMinus],
  templateUrl: './checkbox.component.html',
  styleUrl: './checkbox.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CheckboxComponent),
      multi: true,
    },
  ],
})
export class CheckboxComponent implements ControlValueAccessor {
  @Input() label?: string;
  @Input() description?: string;
  @Input() checked: boolean = false;
  @Input() indeterminate: boolean = false;
  @Input() disabled: boolean = false;
  @Input() errorMessage?: string;
  @Input() size: 'sm' | 'md' = 'md';
  @Input() id: string = `saku-checkbox-${nextUniqueCheckboxId++}`;

  @Output() change = new EventEmitter<boolean>();

  onChange: (value: boolean) => void = () => {};
  onTouched: () => void = () => {};

  writeValue(value: any): void {
    this.checked = !!value;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  toggle(): void {
    if (this.disabled) return;
    this.checked = !this.checked;
    this.indeterminate = false;
    this.onChange(this.checked);
    this.change.emit(this.checked);
    this.onTouched();
  }

  get boxClasses(): string {
    const classes = [
      'relative flex items-center justify-center rounded transition-all duration-150 shrink-0 border cursor-pointer select-none',
    ];

    // Sizes
    if (this.size === 'sm') {
      classes.push('w-4 h-4 text-xs');
    } else {
      classes.push('w-4.5 h-4.5 text-sm');
    }

    if (this.disabled) {
      classes.push('bg-neutral-0 border-neutral-20 opacity-60 cursor-not-allowed');
    } else if (this.checked || this.indeterminate) {
      classes.push('bg-primary border-primary text-white shadow-xs');
    } else if (this.errorMessage) {
      classes.push('bg-white border-error text-error focus:ring-2 focus:ring-error/20');
    } else {
      classes.push('bg-white border-neutral-30 hover:border-primary focus:ring-2 focus:ring-primary/20');
    }

    return classes.join(' ');
  }
}
