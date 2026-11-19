export const LEGEND_LOCUS_BROWSER_TITLE = 'QTL Explorer';

export const LEGEND_CELL_CLASSES_TITLE = 'Cell Classes';

export const LEGEND_CELL_CLASS_COLUMNS = ['Abbreviation', 'Cell Class'] as const;

export const LEGEND_MULTIPLE_VARIANTS_EXAMPLE_COUNT = 3;

export type LegendTrackGlyph =
  | 'gene-not-in-results'
  | 'gene-in-results'
  | 'gene-selected'
  | 'single-variant'
  | 'multiple-variants'
  | 'intron'
  | 'exon'
  | 'utr';

export type LegendItem =
  | { kind: 'track'; glyph: LegendTrackGlyph; label: string }
  | { kind: 'image'; imagePath: string; label: string };

export interface LegendSection {
  title: string;
  items: LegendItem[];
}

export const LEGEND_SECTIONS: LegendSection[] = [
  {
    title: 'Chromosome bar (upper)',
    items: [
      { kind: 'track', glyph: 'gene-not-in-results', label: 'A gene not shown in the results' },
      { kind: 'track', glyph: 'gene-in-results', label: 'A gene shown in the results' },
      { kind: 'track', glyph: 'gene-selected', label: 'The selected gene' },
      { kind: 'track', glyph: 'single-variant', label: 'A single variant' },
      { kind: 'track', glyph: 'multiple-variants', label: 'Multiple variants' },
    ],
  },
  {
    title: 'Gene structure bar (lower)',
    items: [
      { kind: 'track', glyph: 'intron', label: 'Intron' },
      { kind: 'track', glyph: 'exon', label: 'Exon' },
      { kind: 'track', glyph: 'utr', label: '3’ or 5’ UTRs' },
      {
        kind: 'image',
        imagePath: 'qtl-assets/images/transcription-start-site.svg',
        label: 'Transcription start site',
      },
      {
        kind: 'image',
        imagePath: 'qtl-assets/images/transcription-end-site.svg',
        label: 'Transcription end site',
      },
    ],
  },
];
