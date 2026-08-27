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

export type DatePickerMode = 'single' | 'range';

export interface DateRangeValue {
  start: Date | null;
  end: Date | null;
}

export interface DatePreset {
  label: string;
  getValue: () => DateRangeValue | Date;
}

export interface CalendarDay {
  date: Date;
  dayNumber: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  isSelected: boolean;
  isRangeStart: boolean;
  isRangeEnd: boolean;
  isInRange: boolean;
  isDisabled: boolean;
}

let nextUniqueDatePickerId = 0;

@Component({
  selector: 'app-date-picker',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './date-picker.component.html',
  styleUrl: './date-picker.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DatePickerComponent),
      multi: true,
    },
  ],
})
export class DatePickerComponent implements ControlValueAccessor {
  @Input() label?: string;
  @Input() placeholder: string = 'Pilih tanggal';
  @Input() mode: DatePickerMode = 'single';
  @Input() showPresets: boolean = true;
  @Input() disabled: boolean = false;
  @Input() required: boolean = false;
  @Input() minDate?: Date;
  @Input() maxDate?: Date;
  @Input() helperText?: string;
  @Input() errorMessage?: string;
  @Input() id: string = `saku-datepicker-${nextUniqueDatePickerId++}`;

  @Output() dateChange = new EventEmitter<any>();

  isOpen: boolean = false;
  viewDate: Date = new Date();

  // Internal values
  selectedDate: Date | null = null;
  rangeStart: Date | null = null;
  rangeEnd: Date | null = null;
  hoverDate: Date | null = null;
  isPresetMenuOpen: boolean = false;
  selectedPresetLabel: string = 'Periode Kustom (Kalender)';

  readonly dayNames = ['Min', 'Sen', 'Sel', 'Rab', 'Kam', 'Jum', 'Sab'];
  readonly monthNames = [
    'Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni',
    'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember',
  ];

  presets: DatePreset[] = [
    {
      label: 'Hari Ini',
      getValue: () => {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return this.mode === 'range' ? { start: today, end: today } : today;
      },
    },
    {
      label: '7 Hari Terakhir',
      getValue: () => {
        const end = new Date();
        end.setHours(23, 59, 59, 999);
        const start = new Date();
        start.setDate(start.getDate() - 6);
        start.setHours(0, 0, 0, 0);
        return { start, end };
      },
    },
    {
      label: '30 Hari Terakhir',
      getValue: () => {
        const end = new Date();
        end.setHours(23, 59, 59, 999);
        const start = new Date();
        start.setDate(start.getDate() - 29);
        start.setHours(0, 0, 0, 0);
        return { start, end };
      },
    },
    {
      label: 'Bulan Ini',
      getValue: () => {
        const now = new Date();
        const start = new Date(now.getFullYear(), now.getMonth(), 1);
        const end = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59, 999);
        return { start, end };
      },
    },
    {
      label: 'Bulan Lalu',
      getValue: () => {
        const now = new Date();
        const start = new Date(now.getFullYear(), now.getMonth() - 1, 1);
        const end = new Date(now.getFullYear(), now.getMonth(), 0, 23, 59, 59, 999);
        return { start, end };
      },
    },
  ];

  onChange: (value: any) => void = () => {};
  onTouched: () => void = () => {};

  constructor(private elementRef: ElementRef) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isOpen = false;
    }
  }

  toggleOpen(): void {
    if (this.disabled) return;
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      if (this.mode === 'single' && this.selectedDate) {
        this.viewDate = new Date(this.selectedDate);
      } else if (this.mode === 'range' && this.rangeStart) {
        this.viewDate = new Date(this.rangeStart);
      } else {
        this.viewDate = new Date();
      }
    }
  }

  writeValue(value: any): void {
    if (!value) {
      this.selectedDate = null;
      this.rangeStart = null;
      this.rangeEnd = null;
      return;
    }

    if (this.mode === 'single') {
      this.selectedDate = value instanceof Date ? value : new Date(value);
      this.viewDate = new Date(this.selectedDate);
    } else if (this.mode === 'range' && typeof value === 'object') {
      this.rangeStart = value.start ? (value.start instanceof Date ? value.start : new Date(value.start)) : null;
      this.rangeEnd = value.end ? (value.end instanceof Date ? value.end : new Date(value.end)) : null;
      if (this.rangeStart) {
        this.viewDate = new Date(this.rangeStart);
      }
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

  get currentMonthName(): string {
    return this.monthNames[this.viewDate.getMonth()];
  }

  get currentYear(): number {
    return this.viewDate.getFullYear();
  }

  prevMonth(): void {
    this.viewDate = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() - 1, 1);
  }

  nextMonth(): void {
    this.viewDate = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() + 1, 1);
  }

  get calendarDays(): CalendarDay[] {
    const year = this.viewDate.getFullYear();
    const month = this.viewDate.getMonth();

    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);

    const daysInMonth = lastDayOfMonth.getDate();
    const startDayOfWeek = firstDayOfMonth.getDay(); // 0 = Sunday

    const days: CalendarDay[] = [];

    // Previous Month Days (Padding)
    const prevMonthLastDay = new Date(year, month, 0).getDate();
    for (let i = startDayOfWeek - 1; i >= 0; i--) {
      const date = new Date(year, month - 1, prevMonthLastDay - i);
      days.push(this.createCalendarDay(date, false));
    }

    // Current Month Days
    for (let d = 1; d <= daysInMonth; d++) {
      const date = new Date(year, month, d);
      days.push(this.createCalendarDay(date, true));
    }

    // Next Month Days (Padding to fill complete grid of 35 or 42)
    const totalSlots = days.length > 35 ? 42 : 35;
    const remainingSlots = totalSlots - days.length;
    for (let i = 1; i <= remainingSlots; i++) {
      const date = new Date(year, month + 1, i);
      days.push(this.createCalendarDay(date, false));
    }

    return days;
  }

  private createCalendarDay(date: Date, isCurrentMonth: boolean): CalendarDay {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const dTime = new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime();
    const todayTime = today.getTime();

    const isToday = dTime === todayTime;

    let isSelected = false;
    let isRangeStart = false;
    let isRangeEnd = false;
    let isInRange = false;

    if (this.mode === 'single' && this.selectedDate) {
      const sTime = new Date(this.selectedDate.getFullYear(), this.selectedDate.getMonth(), this.selectedDate.getDate()).getTime();
      isSelected = dTime === sTime;
    } else if (this.mode === 'range') {
      const startTime = this.rangeStart ? new Date(this.rangeStart.getFullYear(), this.rangeStart.getMonth(), this.rangeStart.getDate()).getTime() : null;
      const endTime = this.rangeEnd ? new Date(this.rangeEnd.getFullYear(), this.rangeEnd.getMonth(), this.rangeEnd.getDate()).getTime() : null;

      if (startTime && dTime === startTime) {
        isRangeStart = true;
        isSelected = true;
      }
      if (endTime && dTime === endTime) {
        isRangeEnd = true;
        isSelected = true;
      }
      if (startTime && endTime && dTime > startTime && dTime < endTime) {
        isInRange = true;
      } else if (startTime && !endTime && this.hoverDate) {
        const hoverTime = new Date(this.hoverDate.getFullYear(), this.hoverDate.getMonth(), this.hoverDate.getDate()).getTime();
        if (hoverTime > startTime && dTime > startTime && dTime <= hoverTime) {
          isInRange = true;
        }
      }
    }

    let isDisabled = false;
    if (this.minDate) {
      const minTime = new Date(this.minDate.getFullYear(), this.minDate.getMonth(), this.minDate.getDate()).getTime();
      if (dTime < minTime) isDisabled = true;
    }
    if (this.maxDate) {
      const maxTime = new Date(this.maxDate.getFullYear(), this.maxDate.getMonth(), this.maxDate.getDate()).getTime();
      if (dTime > maxTime) isDisabled = true;
    }

    return {
      date,
      dayNumber: date.getDate(),
      isCurrentMonth,
      isToday,
      isSelected,
      isRangeStart,
      isRangeEnd,
      isInRange,
      isDisabled,
    };
  }

  selectDay(day: CalendarDay): void {
    if (day.isDisabled) return;

    if (this.mode === 'single') {
      this.selectedDate = new Date(day.date);
      this.selectedPresetLabel = 'Periode Kustom (Kalender)';
      this.isPresetMenuOpen = false;
      this.emitValue(this.selectedDate);
      this.isOpen = false;
    } else if (this.mode === 'range') {
      this.selectedPresetLabel = 'Periode Kustom (Kalender)';
      this.isPresetMenuOpen = false;
      if (!this.rangeStart || (this.rangeStart && this.rangeEnd)) {
        // Start new range selection
        this.rangeStart = new Date(day.date);
        this.rangeEnd = null;
      } else if (this.rangeStart && !this.rangeEnd) {
        // Finish range selection
        if (day.date < this.rangeStart) {
          this.rangeEnd = this.rangeStart;
          this.rangeStart = new Date(day.date);
        } else {
          this.rangeEnd = new Date(day.date);
        }
        this.emitValue({ start: this.rangeStart, end: this.rangeEnd });
      }
    }
  }

  togglePresetMenu(event?: MouseEvent): void {
    if (event) {
      event.stopPropagation();
    }
    this.isPresetMenuOpen = !this.isPresetMenuOpen;
  }

  selectPreset(preset: DatePreset | null, event?: MouseEvent): void {
    if (event) {
      event.stopPropagation();
    }
    this.isPresetMenuOpen = false;

    if (!preset) {
      this.selectedPresetLabel = 'Periode Kustom (Kalender)';
      return;
    }

    this.selectedPresetLabel = preset.label;
    this.applyPreset(preset);
  }

  applyPreset(preset: DatePreset): void {
    this.selectedPresetLabel = preset.label;
    const val = preset.getValue();
    if (this.mode === 'single') {
      this.selectedDate = val as Date;
      this.viewDate = new Date(this.selectedDate);
      this.emitValue(this.selectedDate);
    } else {
      const range = val as DateRangeValue;
      this.rangeStart = range.start;
      this.rangeEnd = range.end;
      if (this.rangeStart) {
        this.viewDate = new Date(this.rangeStart);
      }
      this.emitValue({ start: this.rangeStart, end: this.rangeEnd });
    }
    this.isOpen = false;
  }

  applyRange(): void {
    if (this.mode === 'range' && this.rangeStart) {
      if (!this.rangeEnd) {
        this.rangeEnd = new Date(this.rangeStart);
      }
      this.emitValue({ start: this.rangeStart, end: this.rangeEnd });
    }
    this.isOpen = false;
  }

  clear(event?: MouseEvent): void {
    if (event) {
      event.stopPropagation();
    }
    this.selectedPresetLabel = 'Periode Kustom (Kalender)';
    this.isPresetMenuOpen = false;
    this.selectedDate = null;
    this.rangeStart = null;
    this.rangeEnd = null;
    if (this.mode === 'single') {
      this.emitValue(null);
    } else {
      this.emitValue({ start: null, end: null });
    }
  }

  private emitValue(val: any): void {
    this.onChange(val);
    this.dateChange.emit(val);
    this.onTouched();
  }

  get displayValue(): string {
    if (this.mode === 'single') {
      return this.selectedDate ? this.formatDate(this.selectedDate) : '';
    } else {
      if (this.rangeStart && this.rangeEnd) {
        return `${this.formatDate(this.rangeStart)} - ${this.formatDate(this.rangeEnd)}`;
      } else if (this.rangeStart) {
        return `${this.formatDate(this.rangeStart)} - ...`;
      }
      return '';
    }
  }

  get hasValue(): boolean {
    return this.mode === 'single' ? !!this.selectedDate : !!(this.rangeStart || this.rangeEnd);
  }

  private formatDate(d: Date): string {
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    return `${day}/${month}/${year}`;
  }
}
