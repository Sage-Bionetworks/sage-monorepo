/**
 * Resolves AI/LLM organization names to brand-mark SVG URLs on the LobeHub
 * icons CDN. Prefers `-color` (full-color brand) variants when available;
 * falls back to the monochrome variant for brands LobeHub publishes only as a
 * single-color mark.
 *
 * Source: @lobehub/icons-static-svg (https://github.com/lobehub/lobe-icons),
 * served via jsDelivr. MIT licensed.
 */

const LOBEHUB_VERSION = '1.87.0';
const LOBEHUB_BASE = `https://cdn.jsdelivr.net/npm/@lobehub/icons-static-svg@${LOBEHUB_VERSION}/icons`;

type LogoVariant = 'color' | 'mono';

/**
 * Single source of truth: each known org slug mapped to the variant LobeHub
 * publishes for that brand. `mono` brands ship only `<slug>.svg` with a
 * monochrome mark; `color` brands also ship `<slug>-color.svg`.
 */
const ORG_LOGO_VARIANT: Readonly<Record<string, LogoVariant>> = Object.freeze({
  google: 'color',
  gemini: 'color',
  meta: 'color',
  mistral: 'color',
  deepseek: 'color',
  qwen: 'color',
  zhipu: 'color',
  cohere: 'color',
  perplexity: 'color',
  microsoft: 'color',
  anthropic: 'mono',
  openai: 'mono',
  xai: 'mono',
  moonshot: 'mono',
});

/**
 * Resolve a free-text org name to a canonical logo slug. Returns `null` when
 * the name is empty or has no recognizable match.
 *
 * Strategy: lowercase + strip non-alphanumerics, then try a direct slug
 * match. If that fails and the key ends in `ai`, retry with the suffix
 * stripped — this catches "Moonshot AI", "Zhipu AI", "Mistral AI", etc.
 * Direct match runs first, so "OpenAI" and "xAI" resolve to themselves
 * before the suffix-strip fallback could damage them.
 */
export function slugifyOrg(name: string | null | undefined): string | null {
  if (!name) return null;
  const slug = name.toLowerCase().replace(/[^a-z0-9]/g, '');
  if (!slug) return null;
  if (slug in ORG_LOGO_VARIANT) return slug;
  if (slug.endsWith('ai') && slug.length > 2) {
    const withoutAiSuffix = slug.slice(0, -2);
    if (withoutAiSuffix in ORG_LOGO_VARIANT) return withoutAiSuffix;
  }
  return null;
}

/**
 * Resolve a free-text org name to a brand-mark image URL. Returns `null`
 * when the org is unknown — callers should render the avatar's initials
 * fallback in that case.
 */
export function getOrgLogoUrl(name: string | null | undefined): string | null {
  const slug = slugifyOrg(name);
  if (!slug) return null;
  return ORG_LOGO_VARIANT[slug] === 'color'
    ? `${LOBEHUB_BASE}/${slug}-color.svg`
    : `${LOBEHUB_BASE}/${slug}.svg`;
}

/**
 * Whether the org's brand mark is a monochrome SVG (renders black inside an
 * `<img>`). Useful for callers that want to invert in dark mode so the mark
 * stays visible against a dark background.
 */
export function isMonoOrgLogo(name: string | null | undefined): boolean {
  const slug = slugifyOrg(name);
  return slug !== null && ORG_LOGO_VARIANT[slug] === 'mono';
}
