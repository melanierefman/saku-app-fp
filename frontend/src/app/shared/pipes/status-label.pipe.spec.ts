import { StatusLabelPipe } from './status-label.pipe';

describe('StatusLabelPipe', () => {
  let pipe: StatusLabelPipe;

  beforeEach(() => {
    pipe = new StatusLabelPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform loan application statuses correctly', () => {
    expect(pipe.transform('MENUNGGU_REVIEW')).toBe('Menunggu Review');
    expect(pipe.transform('PENDING')).toBe('Menunggu Review');
    expect(pipe.transform('DISETUJUI_BM')).toBe('Disetujui BM');
    expect(pipe.transform('DITOLAK_BM')).toBe('Ditolak BM');
    expect(pipe.transform('MENUNGGU_PENCAIRAN')).toBe('Siap Dicairkan');
    expect(pipe.transform('DICAIRKAN')).toBe('Sudah Dicairkan');
  });

  it('should transform KYC and user statuses correctly', () => {
    expect(pipe.transform('VERIFIED')).toBe('Terverifikasi');
    expect(pipe.transform('MENUNGGU_VERIFIKASI')).toBe('Menunggu Verifikasi');
    expect(pipe.transform('AKTIF')).toBe('Aktif');
    expect(pipe.transform('NONAKTIF')).toBe('Nonaktif');
  });

  it('should return dash (-) for null or empty status', () => {
    expect(pipe.transform(null)).toBe('-');
    expect(pipe.transform(undefined)).toBe('-');
    expect(pipe.transform('')).toBe('-');
  });
});
