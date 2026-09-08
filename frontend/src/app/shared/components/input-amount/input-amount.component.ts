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
import {
  LucideBanknote,
  LucideX,
  LucideCircleAlert,
} from '@lucide/angular';

let nextUniqueAmountId = 0;

@Component({
  selector: 'app-input-amount',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideBanknote,
    LucideX,
    LucideCircleAlert,
  ],
  templateUrl: './input-amount.component.html',
  styleUrl: './input-amount.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputAmountComponent),
      multi: true,
    },
  ],
})
export class InputAmountComponent implements ControlValueAccessor {
  @Input() label?: string;
  @Input() placeholder: string = 'Enter the amount';
  @Input() currencyPrefix?: string;
  @Input() helperText?: string;
  @Input() errorMessage?: string;
  @Input() disabled: boolean = false;
  @Input() readonly: boolean = false;
  @Input() required: boolean = false;
  @Input() tooltip?: string;
  @Input() min: number = 0;
  @Input() max?: number;
  @Input() quickAmounts: number[] = [];
  @Input() id: string = `saku-amount-${nextUniqueAmountId++}`;

  @Input()
  set value(val: number | string | null | undefined) {
    this.writeValue(val);
  }
  get value(): number | null {
    return this.rawNumber;
  }

  @Output() amountChange = new EventEmitter<number>();

  rawNumber: number | null = null;
  displayValue: string = '';

  onChange: (value: number | null) => void = () => {};
  onTouched: () => void = () => {};

  writeValue(value: any): void {
    if (value === null || value === undefined || value === '') {
      this.rawNumber = null;
      this.displayValue = '';
    } else {
      const num = Number(value);
      this.rawNumber = isNaN(num) ? null : num;
      this.displayValue = this.formatNumber(this.rawNumber);
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

  onInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    // Extract only digits
    const cleanedDigits = input.value.replace(/\D/g, '');

    if (!cleanedDigits) {
      this.rawNumber = null;
      this.displayValue = '';
    } else {
      let num = parseInt(cleanedDigits, 10);
      if (this.max !== undefined && num > this.max) {
        num = this.max;
      }
      this.rawNumber = num;
      this.displayValue = this.formatNumber(num);
    }

    input.value = this.displayValue;
    this.onChange(this.rawNumber);
    this.amountChange.emit(this.rawNumber ?? 0);
  }

  onBlur(): void {
    this.onTouched();
  }

  selectQuickAmount(amount: number): void {
    if (this.disabled || this.readonly) return;
    this.rawNumber = amount;
    this.displayValue = this.formatNumber(amount);
    this.onChange(this.rawNumber);
    this.amountChange.emit(this.rawNumber);
  }

  addQuickAmount(amount: number): void {
    if (this.disabled || this.readonly) return;
    const current = this.rawNumber ?? 0;
    const next = current + amount;
    this.selectQuickAmount(this.max !== undefined && next > this.max ? this.max : next);
  }

  clearAmount(): void {
    if (this.disabled || this.readonly) return;
    this.rawNumber = null;
    this.displayValue = '';
    this.onChange(null);
    this.amountChange.emit(0);
  }

  formatQuickLabel(amount: number): string {
    if (amount >= 1000000) {
      const jt = amount / 1000000;
      return `+${jt % 1 === 0 ? jt : jt.toFixed(1)} Jt`;
    }
    if (amount >= 1000) {
      return `+${amount / 1000} rb`;
    }
    return `+${amount}`;
  }

  private formatNumber(num: number | null): string {
    if (num === null || isNaN(num)) return '';
    return new Intl.NumberFormat('id-ID').format(num);
  }

  get wrapperClasses(): string {
    const classes = [
      'relative flex items-center w-full rounded-lg saku-input-control',
    ];

    if (this.disabled) {
      classes.push('is-disabled');
    } else if (this.errorMessage) {
      classes.push('is-error');
    } else if (this.rawNumber !== null && this.rawNumber !== undefined && this.rawNumber > 0) {
      classes.push('has-value');
    }

    return classes.join(' ');
  }
}
