import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { SvgIconService } from './svg-icon.service';

describe('SvgIconService', () => {
  let service: SvgIconService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), SvgIconService],
    });
    service = TestBed.inject(SvgIconService);
  });

  it('should load SVG if imagePath is valid', async () => {
    // Test valid path
    expect(service.isValidImagePath('agora-assets/icons/cog.svg')).toBe(true);
    expect(service.isValidImagePath('model-ad-assets/icons/cog.svg')).toBe(true);
    expect(service.isValidImagePath('explorers-assets/icons/cog.svg')).toBe(true);
  });

  it('should not load SVG if imagePath is invalid', async () => {
    // Test invalid paths
    expect(service.isValidImagePath('https://external-domain.com/icon.svg')).toBe(false);
    expect(service.isValidImagePath('/some-other-path/icon.svg')).toBe(false);
    expect(service.isValidImagePath('some-other-path/icon.svg')).toBe(false);
    expect(service.isValidImagePath('')).toBe(false);
  });
});
