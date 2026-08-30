export interface Role {
  id: string;
  nama: string;
  code?: string;
  deskripsi?: string;
  status?: boolean;
}

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
      return 'Marketing'
    default:
      return raw
        .replace(/[_\-]+/g, ' ')
        .split(' ')
        .filter(Boolean)
        .map((w) => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
        .join(' ');
  }
}
