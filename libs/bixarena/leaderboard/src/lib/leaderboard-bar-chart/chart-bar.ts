export interface ChartBar {
  readonly id: string;
  readonly rank: number;
  readonly slug: string;
  readonly modelName: string;
  readonly modelOrganization: string | null;
  readonly score: number;
  // Bar height as a percentage of the track. Floored so the shortest bar still fits the logo.
  readonly heightPct: number;
  // CSS gradient. Ranks 1-3 use the brand podium gradient; rank 4+ use the silver gradient.
  readonly barGradient: string;
  readonly orgLogoUrl: string | null;
  readonly orgLogoMono: boolean;
}
