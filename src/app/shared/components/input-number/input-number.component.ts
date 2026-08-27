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

let nextUniqueNumberId = 0;

@Component({
  selector: 'app-input-number',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './input-number.component.html',
  styleUrl: './input-number.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputNumberComponent),
      multi: true,
    },
  ],
})
export class InputNumberComponent implements ControlValueAccessor {
  @Input() label?: string;
  @Input() placeholder: string = '0';
  @Input() helperText?: string;
  @Input() errorMessage?: string;
  @Input() disabled: boolean = false;
  @Input() readonly: boolean = false;
  @Input() required: boolean = false;
  @Input() min: number = 0;
  @Input() max?: number;
  @Input() step: number = 1;
  @Input() suffix?: string;
  @Input() tooltip?: string;
  @Input() id: string = `saku-number-${nextUniqueNumberId++}`;

  @Input()
  set value(val: number | string | null | undefined) {
    this.writeValue(val);
  }
  get value(): number | null {
    return this.numValue;
  }

  @Output() numberChange = new EventEmitter<number>();

  numValue: number | null = null;

  onChange: (value: number | null) => void = () => {};
  onTouched: () => void = () => {};

  writeValue(value: any): void {
    if (value === null || value === undefined || value === '') {
      this.numValue = null;
    } else {
      const num = Number(value);
      this.numValue = isNaN(num) ? null : num;
    }
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

  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.value === '') {
      this.numValue = null;
    } else {
      let num = Number(input.value);
      if (isNaN(num)) {
        this.numValue = null;
      } else {
        if (this.max !== undefined && num > this.max) num = this.max;
        if (this.min !== undefined && num < this.min) num = this.min;
        this.numValue = num;
      }
    }
    this.onChange(this.numValue);
    this.numberChange.emit(this.numValue ?? 0);
  }

  onBlur(): void {
    this.onTouched();
  }

  increment(): void {
    if (this.disabled || this.readonly) return;
    const current = this.numValue ?? this.min ?? 0;
    let next = current + this.step;
    if (this.max !== undefined && next > this.max) next = this.max;
    this.numValue = next;
    this.onChange(this.numValue);
    this.numberChange.emit(this.numValue);
    this.onTouched();
  }

  decrement(): void {
    if (this.disabled || this.readonly) return;
    const current = this.numValue ?? (this.min !== undefined ? this.min : 0);
    let next = current - this.step;
    if (this.min !== undefined && next < this.min) next = this.min;
    this.numValue = next;
    this.onChange(this.numValue);
    this.numberChange.emit(this.numValue);
    this.onTouched();
  }

  get isAtMin(): boolean {
    if (this.min === undefined) return false;
    return (this.numValue ?? this.min) <= this.min;
  }

  get isAtMax(): boolean {
    if (this.max === undefined) return false;
    return (this.numValue ?? 0) >= this.max;
  }

  get wrapperClasses(): string {
    const classes = [
      'relative flex items-center w-full rounded-lg saku-input-control overflow-hidden',
    ];

    if (this.disabled) {
      classes.push('is-disabled');
    } else if (this.errorMessage) {
      classes.push('is-error');
    } else if (this.numValue !== null && this.numValue !== undefined) {
      classes.push('has-value');
    }

    return classes.join(' ');
  }
}
