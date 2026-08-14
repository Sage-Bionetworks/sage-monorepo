import { Component, computed, inject, input } from '@angular/core';
import { HelperService } from '@sagebionetworks/explorers/services';
import { DetailsLabelComponent, DownloadDomImageComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { IndividualData } from '@sagebionetworks/model-ad/api-client';
import { BoxplotData } from '../boxplot/boxplot.component';
import { BoxplotsGridComponent } from '../boxplots-grid/boxplots-grid.component';

/**
 * Common interface for individual expression details data.
 * Both TranscriptomicsIndividual and ProteomicsIndividual satisfy this interface.
 */
export interface IndividualExpressionDetailsData extends BoxplotData {
  ensembl_gene_id: string;
  gene_symbol: string;
  result_order: string[];
  uniprotid?: string;
  display_symbol?: string;
}

function buildCsvColumnHeaders(hasUniprotid: boolean): string[] {
  return [
    'ensembl_gene_id',
    'gene_symbol',
    ...(hasUniprotid ? ['uniprotid'] : []),
    'age',
    'genotype',
    'sex',
    'individual_id',
    'log2_cpm',
  ];
}

export const CSV_COLUMN_HEADERS = buildCsvColumnHeaders(false);
export const CSV_COLUMN_HEADERS_WITH_UNIPROTID = buildCsvColumnHeaders(true);

@Component({
  selector: 'model-ad-individual-expression-details',
  imports: [
    BoxplotsGridComponent,
    DetailsLabelComponent,
    DownloadDomImageComponent,
    LoadingIconComponent,
  ],
  templateUrl: './individual-expression-details.component.html',
  styleUrls: ['./individual-expression-details.component.scss'],
})
export class IndividualExpressionDetailsComponent {
  private readonly helperService = inject(HelperService);

  isLoading = input.required<boolean>();
  data = input<IndividualExpressionDetailsData[] | undefined>();
  modality = input.required<string>();
  tissue = input<string | null>(null);
  modelIdentifier = input<string | null>(null);
  downloadFilenamePrefix = input.required<string>();

  primaryRecord = computed(() => this.data()?.[0]);

  label = computed(() => {
    const record = this.primaryRecord();
    if (!record) return { left: '', right: '' };

    const ensemblGeneId = record.ensembl_gene_id;
    const left = record.display_symbol || record.gene_symbol || ensemblGeneId;
    return {
      left,
      right: left === ensemblGeneId ? '' : ensemblGeneId,
    };
  });

  heading = computed(() => {
    const tissue = this.tissue();
    return `Individual ${this.modality()} Expression${tissue ? ` (${tissue})` : ''}`;
  });

  subtitle = computed(() => this.modelIdentifier() ?? '');

  xAxisOrder = computed(() => this.primaryRecord()?.result_order);

  csvData = computed(() => {
    const records = this.data();
    if (!records) return [];
    return this.convertToCsvData(records);
  });

  filename = computed(() => {
    const record = this.primaryRecord();
    if (!record) return '';

    const geneSymbol = record.gene_symbol || record.ensembl_gene_id;
    const uniprotidSegment = record.uniprotid ? `_${record.uniprotid}` : '';
    const filename = `${this.downloadFilenamePrefix()}_${geneSymbol}${uniprotidSegment}_${this.modelIdentifier()}_${(this.tissue() || '').toLowerCase()}`;
    return this.helperService.cleanFilename(filename);
  });

  convertToCsvData(records: IndividualExpressionDetailsData[]): string[][] {
    const hasUniprotid = Boolean(records[0]?.uniprotid);
    const csvRows: string[][] = [
      hasUniprotid ? CSV_COLUMN_HEADERS_WITH_UNIPROTID : CSV_COLUMN_HEADERS,
    ];

    records.forEach((record) => {
      const baseRow = [
        record.ensembl_gene_id,
        record.gene_symbol,
        ...(hasUniprotid ? [record.uniprotid ?? ''] : []),
        record.age,
      ];
      record.data.forEach((point: IndividualData) => {
        csvRows.push([
          ...baseRow,
          point.genotype,
          point.sex,
          point.individual_id,
          String(point.value),
        ]);
      });
    });

    return csvRows;
  }
}
