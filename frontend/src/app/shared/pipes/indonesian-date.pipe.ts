import { Pipe, PipeTransform } from '@angular/core';
import { formatDate } from '../utils/date.util';

/**
 * Pipe untuk memformat tanggal ke standar format Indonesia.
 * Contoh penggunaan:
 * {{ '2026-08-15T14:30:00Z' | idDate }} -> "15 Agu 2026, 14:30"
 * {{ '2026-08-15T14:30:00Z' | idDate:false }} -> "15 Agu 2026"
 */
@Pipe({
  name: 'idDate',
  standalone: true,
})
export class IndonesianDatePipe implements PipeTransform {
  transform(
    value?: string | Date | null,
    withTime: boolean = true
  ): string {
    return formatDate(value, withTime);
  }
}
