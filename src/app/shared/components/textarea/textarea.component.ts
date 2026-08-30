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

let nextUniqueTextareaId = 0;

@Component({
  selector: 'app-textarea',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideCircleAlert],
  templateUrl: './textarea.component.html',
  styleUrl: './textarea.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TextareaComponent),
      multi: true,
    },
  ],
})
export class TextareaComponent implements ControlValueAccessor {
  @Input() label?: string;
  @Input() placeholder: string = '';
  @Input() rows: number = 4;
  @Input() helperText?: string;
  @Input() errorMessage?: string;
  @Input() disabled: boolean = false;
  @Input() readonly: boolean = false;
  @Input() required: boolean = false;
  @Input() maxLength?: number;
  @Input() showCharCount: boolean = false;
  @Input() resize: 'none' | 'vertical' | 'both' = 'vertical';
  @Input() id: string = `saku-textarea-${nextUniqueTextareaId++}`;

  @Output() valueChange = new EventEmitter<string>();

  @Input()
  set value(val: string | null | undefined) {
    this.writeValue(val);
  }
  get value(): string {
    return this._value;
  }

  private _value: string = '';

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
    const target = event.target as HTMLTextAreaElement;
    this.value = target.value;
    this.onChange(this.value);
    this.valueChange.emit(this.value);
  }

  onBlur(): void {
    this.onTouched();
  }

  get textareaClasses(): string {
    const classes = [
      'w-full rounded-lg p-3 text-sm text-neutral-100 placeholder:text-neutral-30 focus:outline-none saku-input-control',
    ];

    // Resize
    switch (this.resize) {
      case 'none':
        classes.push('resize-none');
        break;
      case 'vertical':
        classes.push('resize-y');
        break;
      case 'both':
        classes.push('resize');
        break;
    }

    if (this.disabled) {
      classes.push('is-disabled');
    } else if (this.errorMessage) {
      classes.push('is-error');
    } else if (this.value && this.value.trim().length > 0) {
      classes.push('has-value');
    }

    return classes.join(' ');
  }
}
