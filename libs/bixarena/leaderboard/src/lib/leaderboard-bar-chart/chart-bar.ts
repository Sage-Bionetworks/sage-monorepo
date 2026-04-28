// View-model for one bar in the leaderboard chart. Computed from a LeaderboardEntry.
export interface ChartBar {
  readonly id: string;
  readonly rank: number;
  // Model slug (e.g. "claude-opus-4.1"); shown beneath the bar.
  readonly slug: string;
  // Full model name (e.g. "Anthropic: Claude Opus 4.1"); used for hover title and aria-label.
  readonly modelName: string;
  readonly modelOrganization: string | null;
  // Rounded BT score; rendered above the bar tip.
  readonly score: number;
  // Bar height as a percentage of the chart track. Floored so the shortest bar still has room for the logo.
  readonly heightPct: number;
  // CSS gradient string for the bar fill. Top-3 ranks use brand colors; rank 4+ use silver/slate.
  readonly barGradient: string;
  readonly orgLogoUrl: string | null;
  // True for monochrome SVG logos that need dark-mode inversion via CSS filter.
  readonly orgLogoMono: boolean;
}
