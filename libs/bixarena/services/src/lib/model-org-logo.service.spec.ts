import { TestBed } from '@angular/core/testing';
import { ModelOrgLogoService } from './model-org-logo.service';

describe('ModelOrgLogoService', () => {
  let service: ModelOrgLogoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModelOrgLogoService);
  });

  describe('slugify', () => {
    it('lowercases and strips punctuation/whitespace for direct matches', () => {
      expect(service.slugify('Anthropic')).toBe('anthropic');
      expect(service.slugify('  DeepSeek  ')).toBe('deepseek');
      expect(service.slugify('Open AI')).toBe('openai');
    });

    it('does not strip "AI" when a direct slug match exists (OpenAI, xAI)', () => {
      expect(service.slugify('OpenAI')).toBe('openai');
      expect(service.slugify('xAI')).toBe('xai');
    });

    it('strips a trailing "AI" suffix as a fallback (Moonshot AI, Zhipu AI)', () => {
      expect(service.slugify('Moonshot AI')).toBe('moonshot');
      expect(service.slugify('Zhipu AI')).toBe('zhipu');
      expect(service.slugify('Mistral AI')).toBe('mistral');
    });

    it('returns null for empty / whitespace input', () => {
      expect(service.slugify('')).toBeNull();
      expect(service.slugify(null)).toBeNull();
      expect(service.slugify(undefined)).toBeNull();
      expect(service.slugify('   ')).toBeNull();
    });

    it('returns null for an unknown org', () => {
      expect(service.slugify('Acme Quantum')).toBeNull();
    });
  });

  describe('getLogoUrl', () => {
    it('returns the -color URL for brands with a colorful variant', () => {
      expect(service.getLogoUrl('Google')).toContain('google-color.svg');
      expect(service.getLogoUrl('Mistral AI')).toContain('mistral-color.svg');
      expect(service.getLogoUrl('DeepSeek')).toContain('deepseek-color.svg');
    });

    it('returns the mono URL for brands with no color variant', () => {
      expect(service.getLogoUrl('Anthropic')).toContain('anthropic.svg');
      expect(service.getLogoUrl('Anthropic')).not.toContain('-color');
      expect(service.getLogoUrl('OpenAI')).toContain('openai.svg');
      expect(service.getLogoUrl('xAI')).toContain('xai.svg');
      expect(service.getLogoUrl('Moonshot AI')).toContain('moonshot.svg');
      expect(service.getLogoUrl('Moonshot AI')).not.toContain('-color');
    });

    it('returns null for unknown / empty input', () => {
      expect(service.getLogoUrl('Acme Quantum')).toBeNull();
      expect(service.getLogoUrl('')).toBeNull();
      expect(service.getLogoUrl(null)).toBeNull();
    });
  });

  describe('isMonoLogo', () => {
    it('returns true for the four mono-only LobeHub brands', () => {
      expect(service.isMonoLogo('Anthropic')).toBe(true);
      expect(service.isMonoLogo('OpenAI')).toBe(true);
      expect(service.isMonoLogo('xAI')).toBe(true);
      expect(service.isMonoLogo('Moonshot AI')).toBe(true);
    });

    it('returns false for brands that ship a color variant', () => {
      expect(service.isMonoLogo('Google')).toBe(false);
      expect(service.isMonoLogo('DeepSeek')).toBe(false);
      expect(service.isMonoLogo('Qwen')).toBe(false);
      expect(service.isMonoLogo('Zhipu AI')).toBe(false);
    });

    it('returns false for unknown / empty input', () => {
      expect(service.isMonoLogo('Acme Quantum')).toBe(false);
      expect(service.isMonoLogo('')).toBe(false);
      expect(service.isMonoLogo(null)).toBe(false);
    });
  });
});
