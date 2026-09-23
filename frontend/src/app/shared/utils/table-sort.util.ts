/**
 * Utility helper for robust table sorting across the application.
 * Handles dates (chronological), numbers/currency (numeric value),
 * strings (case-insensitive localeCompare), and custom extractor functions.
 * Also guarantees that null/undefined/empty values are placed at the bottom.
 */

export interface SortOptions<T> {
  key: string;
  direction: 'asc' | 'desc';
  extractors?: Record<string, (item: T) => any>;
}

export function sortTableData<T>(
  items: T[],
  key: string,
  direction: 'asc' | 'desc' | '' = 'asc',
  extractors?: Record<string, (item: T) => any>
): T[] {
  if (!key || !direction || !items || items.length <= 1) {
    return items ? [...items] : [];
  }

  const isAsc = direction === 'asc';

  return [...items].sort((a: T, b: T) => {
    let valA: any;
    let valB: any;

    if (extractors && typeof extractors[key] === 'function') {
      valA = extractors[key](a);
      valB = extractors[key](b);
    } else {
      valA = (a as any)[key];
      valB = (b as any)[key];
    }

    // Handle null / undefined / empty values - always push to bottom
    const isValAEmpty =
      valA === null || valA === undefined || valA === '' || valA === '-';
    const isValBEmpty =
      valB === null || valB === undefined || valB === '' || valB === '-';

    if (isValAEmpty && isValBEmpty) return 0;
    if (isValAEmpty) return 1; // Empty always last
    if (isValBEmpty) return -1; // Empty always last

    // Date comparison (checks for Date objects or ISO/date-like strings)
    const dateA = toDateTimestamp(valA);
    const dateB = toDateTimestamp(valB);
    if (dateA !== null && dateB !== null) {
      return isAsc ? dateA - dateB : dateB - dateA;
    }

    // Numeric comparison
    const numA = typeof valA === 'number' ? valA : parseNumericValue(valA);
    const numB = typeof valB === 'number' ? valB : parseNumericValue(valB);
    if (numA !== null && numB !== null) {
      return isAsc ? numA - numB : numB - numA;
    }

    // Boolean comparison
    if (typeof valA === 'boolean' || typeof valB === 'boolean') {
      const boolA = valA ? 1 : 0;
      const boolB = valB ? 1 : 0;
      return isAsc ? boolA - boolB : boolB - boolA;
    }

    // String comparison (case-insensitive)
    const strA = String(valA).trim();
    const strB = String(valB).trim();
    const cmp = strA.localeCompare(strB, 'id-ID', {
      numeric: true,
      sensitivity: 'base',
    });

    return isAsc ? cmp : -cmp;
  });
}

function toDateTimestamp(val: any): number | null {
  if (val instanceof Date) {
    return isNaN(val.getTime()) ? null : val.getTime();
  }
  if (typeof val === 'string') {
    // Only parse if it looks like a date/time string (ISO or YYYY-MM-DD or contains date markers)
    if (
      /^\d{4}-\d{2}-\d{2}/.test(val) ||
      /^\d{2}\s+(Jan|Feb|Mar|Apr|Mei|Jun|Jul|Agu|Sep|Okt|Nov|Des)/i.test(val) ||
      /^\d{4}\/\d{2}\/\d{2}/.test(val)
    ) {
      const parsed = Date.parse(val);
      if (!isNaN(parsed)) return parsed;
    }
  }
  return null;
}

function parseNumericValue(val: any): number | null {
  if (typeof val === 'number') return isNaN(val) ? null : val;
  if (typeof val === 'string') {
    // Clean currency or format like "Rp 10.000.000" or "75%" or "12 Bulan"
    const cleaned = val
      .replace(/^Rp\s?/i, '')
      .replace(/\s?Bulan/i, '')
      .replace(/%/g, '')
      .replace(/\./g, '')
      .replace(/,/g, '.')
      .trim();

    if (/^-?\d+(\.\d+)?$/.test(cleaned)) {
      const num = parseFloat(cleaned);
      return isNaN(num) ? null : num;
    }
  }
  return null;
}
