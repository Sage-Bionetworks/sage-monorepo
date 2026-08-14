import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render, screen } from '@testing-library/angular';
import {
  CSV_COLUMN_HEADERS,
  CSV_COLUMN_HEADERS_WITH_UNIPROTID,
  IndividualExpressionDetailsComponent,
  IndividualExpressionDetailsData,
} from './individual-expression-details.component';

const mockRecord: IndividualExpressionDetailsData = {
  ensembl_gene_id: 'ENSMUSG00000001',
  gene_symbol: 'TestGene',
  units: 'Log2 Counts per Million',
  age: '6 months',
  result_order: ['ControlModel', 'TestModel'],
  data: [
    { genotype: 'ControlModel', sex: 'Female', individual_id: '001', value: 1.23 },
    { genotype: 'TestModel', sex: 'Male', individual_id: '002', value: 4.56 },
  ],
};

const mockOlderRecord: IndividualExpressionDetailsData = {
  ...mockRecord,
  age: '12 months',
  data: [{ genotype: 'ControlModel', sex: 'Male', individual_id: '003', value: 7.89 }],
};

const mockRecordWithZeroValue: IndividualExpressionDetailsData = {
  ...mockRecord,
  data: [{ genotype: 'ControlModel', sex: 'Female', individual_id: '001', value: 0 }],
};

const mockRecordWithUniprotid: IndividualExpressionDetailsData = {
  ...mockRecord,
  uniprotid: 'B9EKJ1',
  display_symbol: 'TestGene (B9EKJ1)',
};

const mockModality = 'RNA';
const mockTissue = 'Hippocampus';
const mockModelIdentifier = 'TestModel';

async function setup({
  isLoading = false,
  data = [mockRecord],
  tissue = mockTissue as string | null,
} = {}) {
  return render(IndividualExpressionDetailsComponent, {
    inputs: {
      isLoading,
      data,
      tissue,
      modality: mockModality,
      modelIdentifier: mockModelIdentifier,
      downloadFilenamePrefix: 'expression_individual',
    },
    providers: [provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS)],
  });
}

describe('IndividualExpressionDetailsComponent', () => {
  it('should show the loading icon while loading', async () => {
    const { container } = await setup({ isLoading: true });
    expect(container.querySelector('.loading-icon')).toBeVisible();
  });

  it('should render nothing when there are no records', async () => {
    const { container } = await setup({ data: [] });
    expect(screen.queryByRole('heading', { level: 2 })).not.toBeInTheDocument();
    expect(container.querySelector('model-ad-boxplots-grid')).toBeNull();
  });

  it('should build the heading from the modality and tissue', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 2 })).toHaveTextContent(
      'Individual RNA Expression (Hippocampus)',
    );
  });

  it('should omit the parentheses in the heading when there is no tissue', async () => {
    await setup({ tissue: null });
    expect(screen.getByRole('heading', { level: 2 }).textContent?.trim()).toBe(
      'Individual RNA Expression',
    );
  });

  it('should display the model identifier as the subtitle', async () => {
    await setup();
    expect(screen.getByText(mockModelIdentifier)).toBeInTheDocument();
  });

  it('should display the boxplots grid', async () => {
    const { container } = await setup();
    expect(container.querySelector('model-ad-boxplots-grid')).not.toBeNull();
  });

  describe('label', () => {
    it('should show the gene symbol alongside the ensembl gene id', async () => {
      await setup();
      expect(screen.getByText(mockRecord.ensembl_gene_id, { exact: false })).toHaveTextContent(
        `${mockRecord.gene_symbol} | ${mockRecord.ensembl_gene_id}`,
      );
    });

    it('should prefer the display symbol when the record has one', async () => {
      await setup({ data: [mockRecordWithUniprotid] });
      expect(screen.getByText(mockRecord.ensembl_gene_id, { exact: false })).toHaveTextContent(
        `${mockRecordWithUniprotid.display_symbol} | ${mockRecord.ensembl_gene_id}`,
      );
    });

    it('should fall back to the gene symbol when the display symbol is blank', async () => {
      await setup({ data: [{ ...mockRecordWithUniprotid, display_symbol: '' }] });
      expect(screen.getByText(mockRecord.ensembl_gene_id, { exact: false })).toHaveTextContent(
        `${mockRecord.gene_symbol} | ${mockRecord.ensembl_gene_id}`,
      );
    });

    it('should show only the ensembl gene id when there is no gene symbol', async () => {
      await setup({ data: [{ ...mockRecord, gene_symbol: '' }] });
      expect(screen.getByText(mockRecord.ensembl_gene_id)).toHaveTextContent(
        mockRecord.ensembl_gene_id,
      );
    });
  });

  describe('download', () => {
    it('should convert each data point of each record to its own CSV row', async () => {
      const { fixture } = await setup({ data: [mockRecord, mockOlderRecord] });
      expect(fixture.componentInstance.csvData()).toEqual([
        CSV_COLUMN_HEADERS,
        ['ENSMUSG00000001', 'TestGene', '6 months', 'ControlModel', 'Female', '001', '1.23'],
        ['ENSMUSG00000001', 'TestGene', '6 months', 'TestModel', 'Male', '002', '4.56'],
        ['ENSMUSG00000001', 'TestGene', '12 months', 'ControlModel', 'Male', '003', '7.89'],
      ]);
    });

    it('should keep a value of zero instead of emitting an empty cell', async () => {
      const { fixture } = await setup({ data: [mockRecordWithZeroValue] });
      expect(fixture.componentInstance.csvData()).toEqual([
        CSV_COLUMN_HEADERS,
        ['ENSMUSG00000001', 'TestGene', '6 months', 'ControlModel', 'Female', '001', '0'],
      ]);
    });

    it('should add a uniprotid column when the records have a uniprotid', async () => {
      const { fixture } = await setup({ data: [mockRecordWithUniprotid] });
      expect(fixture.componentInstance.csvData()).toEqual([
        CSV_COLUMN_HEADERS_WITH_UNIPROTID,
        [
          'ENSMUSG00000001',
          'TestGene',
          'B9EKJ1',
          '6 months',
          'ControlModel',
          'Female',
          '001',
          '1.23',
        ],
        ['ENSMUSG00000001', 'TestGene', 'B9EKJ1', '6 months', 'TestModel', 'Male', '002', '4.56'],
      ]);
    });

    it('should build the filename from the prefix, symbol, model identifier, and tissue', async () => {
      const { fixture } = await setup();
      expect(fixture.componentInstance.filename()).toBe(
        'expression_individual_TestGene_TestModel_hippocampus',
      );
    });

    it('should include the uniprotid in the filename when the record has one', async () => {
      const { fixture } = await setup({ data: [mockRecordWithUniprotid] });
      expect(fixture.componentInstance.filename()).toBe(
        'expression_individual_TestGene_B9EKJ1_TestModel_hippocampus',
      );
    });
  });
});
