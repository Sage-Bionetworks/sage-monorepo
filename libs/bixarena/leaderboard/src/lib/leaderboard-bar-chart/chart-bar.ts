export interface ChartBar {
  readonly id: string;
  readonly rank: number;
  readonly modelName: string;
  readonly modelOrganization: string | null;
  readonly score: number;
  readonly heightPct: number;
  readonly orgLogoUrl: string | null;
  readonly orgLogoMono: boolean;
}
