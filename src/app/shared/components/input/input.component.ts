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
import { LucideCircleAlert } from '@lucide/angular';

export type InputSize = 'sm' | 'md' | 'lg';
export type InputType =
  | 'text'
  | 'password'
  | 'email'
  | 'number'
  | 'search'
  | 'tel'
  | 'url';

let nextUniqueId = 0;

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideCircleAlert],
  templateUrl: './input.component.html',
  styleUrl: './input.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputComponent),
      multi: true,
    },
  ],
})
export class InputComponent implements ControlValueAccessor {
  @Input() label?: string;
  @Input() type: InputType = 'text';
  @Input() placeholder: string = '';
  @Input() helperText?: string;
  @Input() errorMessage?: string;
  @Input() disabled: boolean = false;
  @Input() readonly: boolean = false;
  @Input() required: boolean = false;
  @Input() id: string = `saku-input-${nextUniqueId++}`;
  @Input() size: InputSize = 'md';
  @Input() prefixText?: string;
  @Input() suffixText?: string;

  @Input()
  set value(val: string | number | null | undefined) {
    this.writeValue(val);
  }
  get value(): string {
    return this._value;
  }

  @Output() inputChange = new EventEmitter<string>();
  @Output() inputBlur = new EventEmitter<FocusEvent>();
  @Output() inputFocus = new EventEmitter<FocusEvent>();

  private _value: string = '';

  // ControlValueAccessor methods
  onChange: (value: string) => void = () => {};
  onTouched: () => void = () => {};

  writeValue(value: any): void {
    this._value = value !== null && value !== undefined ? String(value) : '';
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
    const target = event.target as HTMLInputElement;
    this.value = target.value;
    this.onChange(this.value);
    this.inputChange.emit(this.value);
  }

  onBlur(event: FocusEvent): void {
    this.onTouched();
    this.inputBlur.emit(event);
  }

  onFocus(event: FocusEvent): void {
    this.inputFocus.emit(event);
  }

  get effectiveType(): InputType {
    // When password input is empty, render as text so placeholder font metrics and baseline alignment
    // are 100% identical and vertically centered with normal text inputs in Chromium/Safari
    if (this.type === 'password' && !this.value) {
      return 'text';
    }
    return this.type;
  }

  get inputWrapperClasses(): string {
    const classes = [
      'relative flex items-center w-full rounded-xl saku-input-control h-10',
    ];

    if (this.disabled) {
      classes.push('is-disabled');
    } else if (this.errorMessage) {
      classes.push('is-error');
    } else if (this.value && this.value.trim().length > 0) {
      classes.push('has-value');
    }

    return classes.join(' ');
  }

  get inputClasses(): string {
    const classes = [
      'flex-1 min-w-0 w-full bg-transparent text-neutral-100 placeholder:text-neutral-30 focus:outline-none disabled:cursor-not-allowed',
    ];

    switch (this.size) {
      case 'sm':
        classes.push('px-3 text-xs');
        break;
      case 'lg':
        classes.push('px-4 text-base');
        break;
      case 'md':
      default:
        classes.push('px-3.5 text-sm');
        break;
    }

    return classes.join(' ');
  }
}
