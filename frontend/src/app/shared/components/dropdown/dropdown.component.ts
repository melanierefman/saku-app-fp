import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
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
  LucideChevronDown,
  LucideX,
  LucideSearch,
  LucideCheck,
  LucideCircleAlert,
} from '@lucide/angular';

export interface DropdownOption {
  value: any;
  label: string;
  badge?: string;
  disabled?: boolean;
}

let nextUniqueDropdownId = 0;

@Component({
  selector: 'app-dropdown',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideChevronDown,
    LucideX,
    LucideSearch,
    LucideCheck,
    LucideCircleAlert,
  ],
  templateUrl: './dropdown.component.html',
  styleUrl: './dropdown.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DropdownComponent),
      multi: true,
    },
  ],
})
export class DropdownComponent implements ControlValueAccessor {
  @Input() label?: string;
  @Input() placeholder: string = 'Pilih opsi...';
  @Input() options: DropdownOption[] = [];
  @Input() helperText?: string;
  @Input() errorMessage?: string;
  @Input() disabled: boolean = false;
  @Input() required: boolean = false;
  @Input() clearable: boolean = false;
  @Input() searchable: boolean = false;
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() id: string = `saku-dropdown-${nextUniqueDropdownId++}`;

  @Output() optionChange = new EventEmitter<any>();

  selectedValue: any = null;
  isOpen: boolean = false;
  searchQuery: string = '';

  constructor(private elementRef: ElementRef) { }

  onChange: (value: any) => void = () => { };
  onTouched: () => void = () => { };

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

  toggle(): void {
    if (this.disabled) return;
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      this.searchQuery = '';
    } else {
      this.onTouched();
    }
  }

  selectOption(option: DropdownOption): void {
    if (option.disabled) return;
    this.selectedValue = option.value;
    this.onChange(this.selectedValue);
    this.optionChange.emit(this.selectedValue);
    this.isOpen = false;
    this.onTouched();
  }

  clearSelection(event: MouseEvent): void {
    event.stopPropagation();
    this.selectedValue = null;
    this.onChange(null);
    this.optionChange.emit(null);
    this.onTouched();
  }

  get selectedOption(): DropdownOption | undefined {
    return this.options.find((opt) => opt.value === this.selectedValue);
  }

  get filteredOptions(): DropdownOption[] {
    if (!this.searchQuery.trim()) {
      return this.options;
    }
    const query = this.searchQuery.toLowerCase();
    return this.options.filter((opt) => opt.label.toLowerCase().includes(query));
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      if (this.isOpen) {
        this.isOpen = false;
        this.onTouched();
      }
    }
  }

  @HostListener('keydown.escape')
  onEscape(): void {
    if (this.isOpen) {
      this.isOpen = false;
      this.onTouched();
    }
  }

  get triggerClasses(): string {
    const classes = [
      'relative flex items-center justify-between w-full cursor-pointer select-none text-left saku-input-control transition-all',
    ];

    switch (this.size) {
      case 'sm':
        classes.push('py-1.5 px-3 text-xs rounded-lg h-8');
        break;
      case 'lg':
        classes.push('py-2.5 px-4 text-base rounded-xl h-11');
        break;
      case 'md':
      default:
        classes.push('py-2 px-3.5 text-sm rounded-xl h-10');
        break;
    }

    if (this.disabled) {
      classes.push('is-disabled');
    } else if (this.errorMessage) {
      classes.push('is-error');
    } else if (this.isOpen) {
      classes.push('is-open');
    } else if (this.selectedValue !== null && this.selectedValue !== undefined && this.selectedValue !== '') {
      classes.push('has-value');
    }

    return classes.join(' ');
  }
}