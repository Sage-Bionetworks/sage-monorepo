export interface ChartBar {
  readonly id: string;
  readonly rank: number;
  readonly slug: string;
  readonly modelName: string;
  readonly modelOrganization: string | null;
  readonly score: number;
  readonly heightPct: number;
  readonly barGradient: string;
  readonly orgLogoUrl: string | null;
  readonly orgLogoMono: boolean;
}
