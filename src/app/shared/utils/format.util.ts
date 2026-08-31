// Format angka ke format Rupiah (IDR). Contoh: 1500000 -> "Rp 1.500.000"
export function formatRupiah(
  amount?: number | string | null,
  withPrefix: boolean = true
): string {
  if (amount === undefined || amount === null || amount === '') return '-';
  const num = typeof amount === 'string' ? parseFloat(amount) : amount;
  if (isNaN(num)) return '-';

  const formatted = new Intl.NumberFormat('id-ID', {
    maximumFractionDigits: 0,
  }).format(num);

  return withPrefix ? `Rp ${formatted}` : formatted;
}

// Format nama role ke bentuk yang rapi dan mudah dibaca.
// Contoh: "BRANCHMANAGER" -> "Branch Manager"
export function formatRoleName(role?: string | null): string {
  if (!role) return '-';
  const raw = role.trim();
  const clean = raw.toUpperCase().replace(/[\s\-_]+/g, '');

  switch (clean) {
    case 'BACKOFFICE':
      return 'Back Office';
    case 'BRANCHMANAGER':
      return 'Branch Manager';
    case 'SUPERADMIN':
      return 'Superadmin';
    case 'MARKETING':
      return 'Marketing';
    default:
      return raw
        .replace(/[_\-]+/g, ' ')
        .split(' ')
        .filter(Boolean)
        .map((w) => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
        .join(' ');
  }
}
