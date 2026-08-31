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

let nextUniqueRadioId = 0;

@Component({
  selector: 'app-radio',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './radio.component.html',
  styleUrl: './radio.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => RadioComponent),
      multi: true,
    },
  ],
})
export class RadioComponent implements ControlValueAccessor {
  @Input() name?: string;
  @Input() value: any;
  @Input() label?: string;
  @Input() description?: string;
  @Input() disabled: boolean = false;
  @Input() size: 'sm' | 'md' = 'md';
  @Input() id: string = `saku-radio-${nextUniqueRadioId++}`;

  @Output() select = new EventEmitter<any>();

  selectedValue: any;

  onChange: (value: any) => void = () => {};
  onTouched: () => void = () => {};

  writeValue(value: any): void {
    this.selectedValue = value;
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

  onSelect(): void {
    if (this.disabled) return;
    this.selectedValue = this.value;
    this.onChange(this.value);
    this.select.emit(this.value);
    this.onTouched();
  }

  get isChecked(): boolean {
    if (this.selectedValue === this.value) return true;
    if (this.selectedValue !== undefined && this.selectedValue !== null && this.value !== undefined && this.value !== null) {
      return String(this.selectedValue) === String(this.value);
    }
    return false;
  }

  get circleClasses(): string {
    const classes = [
      'relative flex items-center justify-center rounded-full transition-all duration-150 shrink-0 border cursor-pointer select-none',
    ];

    if (this.size === 'sm') {
      classes.push('w-4 h-4');
    } else {
      classes.push('w-4.5 h-4.5');
    }

    if (this.disabled) {
      classes.push('bg-neutral-0 border-neutral-20 opacity-60 cursor-not-allowed');
    } else if (this.isChecked) {
      classes.push('border-primary bg-white shadow-xs');
    } else {
      classes.push('bg-white border-neutral-30 hover:border-primary');
    }

    return classes.join(' ');
  }
}
