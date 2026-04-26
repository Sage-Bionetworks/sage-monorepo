import { getOrgLogoUrl, isMonoOrgLogo, slugifyOrg } from './model-org-logo';

describe('slugifyOrg', () => {
  it('lowercases and strips punctuation/whitespace for direct matches', () => {
    expect(slugifyOrg('Anthropic')).toBe('anthropic');
    expect(slugifyOrg('  DeepSeek  ')).toBe('deepseek');
    expect(slugifyOrg('Open AI')).toBe('openai');
  });

  it('does not strip "AI" when a direct slug match exists (OpenAI, xAI)', () => {
    expect(slugifyOrg('OpenAI')).toBe('openai');
    expect(slugifyOrg('xAI')).toBe('xai');
  });

  it('strips a trailing "AI" suffix as a fallback (Moonshot AI, Zhipu AI)', () => {
    expect(slugifyOrg('Moonshot AI')).toBe('moonshot');
    expect(slugifyOrg('Zhipu AI')).toBe('zhipu');
    expect(slugifyOrg('Mistral AI')).toBe('mistral');
  });

  it('returns null for empty / whitespace input', () => {
    expect(slugifyOrg('')).toBeNull();
    expect(slugifyOrg(null)).toBeNull();
    expect(slugifyOrg(undefined)).toBeNull();
    expect(slugifyOrg('   ')).toBeNull();
  });

  it('returns null for an unknown org', () => {
    expect(slugifyOrg('Acme Quantum')).toBeNull();
  });
});

describe('getOrgLogoUrl', () => {
  it('returns the -color URL for brands with a colorful variant', () => {
    expect(getOrgLogoUrl('Google')).toContain('google-color.svg');
    expect(getOrgLogoUrl('Mistral AI')).toContain('mistral-color.svg');
    expect(getOrgLogoUrl('DeepSeek')).toContain('deepseek-color.svg');
  });

  it('returns the mono URL for brands with no color variant', () => {
    expect(getOrgLogoUrl('Anthropic')).toContain('anthropic.svg');
    expect(getOrgLogoUrl('Anthropic')).not.toContain('-color');
    expect(getOrgLogoUrl('OpenAI')).toContain('openai.svg');
    expect(getOrgLogoUrl('xAI')).toContain('xai.svg');
    expect(getOrgLogoUrl('Moonshot AI')).toContain('moonshot.svg');
    expect(getOrgLogoUrl('Moonshot AI')).not.toContain('-color');
  });

  it('returns null for unknown / empty input', () => {
    expect(getOrgLogoUrl('Acme Quantum')).toBeNull();
    expect(getOrgLogoUrl('')).toBeNull();
    expect(getOrgLogoUrl(null)).toBeNull();
  });
});

describe('isMonoOrgLogo', () => {
  it('returns true for the four mono-only LobeHub brands', () => {
    expect(isMonoOrgLogo('Anthropic')).toBe(true);
    expect(isMonoOrgLogo('OpenAI')).toBe(true);
    expect(isMonoOrgLogo('xAI')).toBe(true);
    expect(isMonoOrgLogo('Moonshot AI')).toBe(true);
  });

  it('returns false for brands that ship a color variant', () => {
    expect(isMonoOrgLogo('Google')).toBe(false);
    expect(isMonoOrgLogo('DeepSeek')).toBe(false);
    expect(isMonoOrgLogo('Qwen')).toBe(false);
    expect(isMonoOrgLogo('Zhipu AI')).toBe(false);
  });

  it('returns false for unknown / empty input', () => {
    expect(isMonoOrgLogo('Acme Quantum')).toBe(false);
    expect(isMonoOrgLogo('')).toBe(false);
    expect(isMonoOrgLogo(null)).toBe(false);
  });
});
