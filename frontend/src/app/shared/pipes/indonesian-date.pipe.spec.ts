import { IndonesianDatePipe } from './indonesian-date.pipe';

describe('IndonesianDatePipe', () => {
  let pipe: IndonesianDatePipe;

  beforeEach(() => {
    pipe = new IndonesianDatePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should format date string with time by default', () => {
    const result = pipe.transform('2026-08-15T14:30:00Z');
    expect(result).toBeTruthy();
    expect(result).not.toBe('-');
  });

  it('should format date string without time when withTime is false', () => {
    const result = pipe.transform('2026-08-15T14:30:00Z', false);
    expect(result).toBeTruthy();
    expect(result).not.toBe('-');
  });

  it('should return dash (-) for null or invalid date', () => {
    expect(pipe.transform(null)).toBe('-');
    expect(pipe.transform(undefined)).toBe('-');
    expect(pipe.transform('')).toBe('-');
  });
});
