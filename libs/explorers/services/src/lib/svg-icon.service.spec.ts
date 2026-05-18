import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { SvgIconService } from './svg-icon.service';
import { SVG_ICON_REGISTRY } from './svg-icon-registry.gen';

describe('SvgIconService', () => {
  let service: SvgIconService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SvgIconService],
    });
    service = TestBed.inject(SvgIconService);
  });

  describe('isValidImagePath', () => {
    it('accepts paths from approved asset directories', () => {
      expect(service.isValidImagePath('agora-assets/icons/cog.svg')).toBe(true);
      expect(service.isValidImagePath('model-ad-assets/icons/cog.svg')).toBe(true);
      expect(service.isValidImagePath('explorers-assets/icons/cog.svg')).toBe(true);
    });

    it('rejects external URLs, absolute paths, and unrecognized prefixes', () => {
      expect(service.isValidImagePath('https://external-domain.com/icon.svg')).toBe(false);
      expect(service.isValidImagePath('/some-other-path/icon.svg')).toBe(false);
      expect(service.isValidImagePath('some-other-path/icon.svg')).toBe(false);
      expect(service.isValidImagePath('')).toBe(false);
    });
  });

  describe('getSvg', () => {
    it('returns sanitized content from the registry for a known path', async () => {
      const path = 'explorers-assets/icons/cog.svg';
      expect(SVG_ICON_REGISTRY[path]).toBeDefined();

      const result = await firstValueFrom(service.getSvg(path));
      expect(result).toBeTruthy();
    });

    it('returns an empty value and warns when the path is not in the registry', async () => {
      const warn = jest.spyOn(console, 'warn').mockImplementation(() => {
        // suppress console output in test
      });
      const result = await firstValueFrom(
        service.getSvg('explorers-assets/icons/does-not-exist.svg'),
      );
      expect(result).toBe('');
      expect(warn).toHaveBeenCalledWith(expect.stringContaining('does-not-exist.svg'));
      warn.mockRestore();
    });

    it('throws on invalid paths', () => {
      expect(() => service.getSvg('https://external.com/icon.svg')).toThrow('Invalid SVG path');
      expect(() => service.getSvg('')).toThrow('Invalid SVG path');
    });
  });
});
