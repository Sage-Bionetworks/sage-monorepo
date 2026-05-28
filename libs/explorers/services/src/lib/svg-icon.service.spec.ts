import { TestBed } from '@angular/core/testing';
import { SvgIconService } from './svg-icon.service';

describe('SvgIconService', () => {
  let service: SvgIconService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SvgIconService],
    });
    service = TestBed.inject(SvgIconService);
  });

  describe('isValidImagePath', () => {
    it('accepts explorers-assets icon paths', () => {
      expect(service.isValidImagePath('explorers-assets/icons/cog.svg')).toBe(true);
      expect(service.isValidImagePath('explorers-assets/icons/card-arrow.svg')).toBe(true);
    });

    it('rejects external URLs, absolute paths, and non-explorers prefixes', () => {
      expect(service.isValidImagePath('https://external-domain.com/icon.svg')).toBe(false);
      expect(service.isValidImagePath('/some-other-path/icon.svg')).toBe(false);
      expect(service.isValidImagePath('some-other-path/icon.svg')).toBe(false);
      expect(service.isValidImagePath('agora-assets/icons/cog.svg')).toBe(false);
      expect(service.isValidImagePath('model-ad-assets/icons/cog.svg')).toBe(false);
      expect(service.isValidImagePath('')).toBe(false);
    });
  });

  describe('getSvg', () => {
    it('returns sanitized SafeHtml for a known registry path', () => {
      const result = service.getSvg('explorers-assets/icons/cog.svg');
      // SafeHtml is a non-empty object — truthy; the empty fallback ('') is falsy
      expect(result).toBeTruthy();
    });

    it('returns SafeHtml and warns when the path is not in the registry', () => {
      const warn = jest.spyOn(console, 'warn').mockImplementation(() => {
        // suppress console output in test
      });
      const result = service.getSvg('explorers-assets/icons/does-not-exist.svg');
      expect(result).toBeTruthy();
      expect(warn).toHaveBeenCalledWith(expect.stringContaining('does-not-exist.svg'));
      warn.mockRestore();
    });

    it('throws on invalid paths', () => {
      expect(() => service.getSvg('https://external.com/icon.svg')).toThrow('Invalid SVG path');
      expect(() => service.getSvg('')).toThrow('Invalid SVG path');
    });
  });
});
