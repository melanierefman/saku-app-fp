export function formatDate(
  dateStr?: string | Date | null,
  withTime: boolean = true
): string {
  if (!dateStr) return '-';
  try {
    let d: Date;
    if (dateStr instanceof Date) {
      d = dateStr;
    } else if (typeof dateStr === 'string') {
      let s = dateStr.trim();
      if (s.includes(' ') && !s.includes('T')) {
        s = s.replace(' ', 'T');
      }
      d = new Date(s);
      if (isNaN(d.getTime())) {
        // Try parsing YYYY-MM-DD or DD/MM/YYYY
        const parts = s.split(/[-/]/);
        if (parts.length >= 3) {
          if (parts[0].length === 4) {
            d = new Date(parseInt(parts[0], 10), parseInt(parts[1], 10) - 1, parseInt(parts[2], 10));
          } else if (parts[2].length === 4) {
            d = new Date(parseInt(parts[2], 10), parseInt(parts[1], 10) - 1, parseInt(parts[0], 10));
          }
        }
      }
    } else {
      d = new Date(dateStr);
    }

    if (isNaN(d.getTime())) return String(dateStr);

    const day = String(d.getDate()).padStart(2, '0');
    const months = [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'Mei',
      'Jun',
      'Jul',
      'Agu',
      'Sep',
      'Okt',
      'Nov',
      'Des',
    ];
    const month = months[d.getMonth()];
    const year = d.getFullYear();

    const hours = d.getHours();
    const mins = d.getMinutes();
    const hasTime = hours !== 0 || mins !== 0;

    if (!withTime || !hasTime) {
      return `${day} ${month} ${year}`;
    }

    const hh = String(hours).padStart(2, '0');
    const mm = String(mins).padStart(2, '0');
    return `${day} ${month} ${year}, ${hh}:${mm}`;
  } catch {
    return String(dateStr);
  }
}

export const formatDateIndo = formatDate;
