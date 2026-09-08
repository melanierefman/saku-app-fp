import { Pipe, PipeTransform } from '@angular/core';
import { formatRupiah } from '../utils/format.util';

/**
 * Pipe untuk memformat angka/string nominal ke format Rupiah (IDR).
 * Contoh penggunaan:
 * {{ 1500000 | rupiah }} -> "Rp 1.500.000"
 * {{ 1500000 | rupiah:false }} -> "1.500.000"
 */
@Pipe({
  name: 'rupiah',
  standalone: true,
})
export class RupiahPipe implements PipeTransform {
  transform(value?: number | string | null, withPrefix: boolean = true): string {
    return formatRupiah(value, withPrefix);
  }
}
