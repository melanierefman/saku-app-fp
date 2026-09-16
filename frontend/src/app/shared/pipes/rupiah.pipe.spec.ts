import { RupiahPipe } from './rupiah.pipe';

describe('RupiahPipe', () => {
  let pipe: RupiahPipe;

  beforeEach(() => {
    pipe = new RupiahPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should format numbers with Rp prefix by default', () => {
    expect(pipe.transform(1500000)).toBe('Rp 1.500.000');
    expect(pipe.transform(0)).toBe('Rp 0');
    expect(pipe.transform(50000000)).toBe('Rp 50.000.000');
  });

  it('should format numbers without prefix when withPrefix is false', () => {
    expect(pipe.transform(1500000, false)).toBe('1.500.000');
    expect(pipe.transform(0, false)).toBe('0');
  });

  it('should handle string numeric values', () => {
    expect(pipe.transform('2500000')).toBe('Rp 2.500.000');
  });

  it('should handle null and undefined safely', () => {
    expect(pipe.transform(null)).toBe('-');
    expect(pipe.transform(undefined)).toBe('-');
  });
});
