import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import {
  GeneExpressionIndividual,
  GeneExpressionIndividualService,
} from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { geneExpressionIndividualMocks } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { of } from 'rxjs';
import { GeneDetailsComponent } from './gene-details.component';

async function setup(
  geneDetails = geneExpressionIndividualMocks,
  platformService: Partial<PlatformService> | null = null,
) {
  const user = userEvent.setup();

  const mockActivatedRoute = {
    paramMap: of(
      convertToParamMap({
        ensemblGeneId: geneDetails[0].ensembl_gene_id,
      }),
    ),
    queryParamMap: of(
      convertToParamMap({
        model: geneDetails[0].name,
        tissue: geneDetails[0].tissue,
      }),
    ),
  };

  const mockGeneExpressionIndividualService = {
    getGeneExpressionIndividual: jest.fn(() => of(geneDetails)),
  };

  const mockPlatformService = platformService || {
    isBrowser: true,
    isServer: false,
  };

  const component = await render(GeneDetailsComponent, {
    imports: [LoadingIconComponent],
    providers: [
      { provide: GeneExpressionIndividualService, useValue: mockGeneExpressionIndividualService },
      { provide: PlatformService, useValue: mockPlatformService },
      {
        provide: ActivatedRoute,
        useValue: mockActivatedRoute,
      },
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
    ],
  });

  return { user, component };
}

describe('GeneDetailsComponent', () => {
  afterAll(() => jest.restoreAllMocks());

  it('should show loading icon on server', async () => {
    const mockPlatformService = {
      isBrowser: false,
      isServer: true,
    };

    const { component } = await setup(geneExpressionIndividualMocks, mockPlatformService);
    expect(component.container.querySelector('.loading-icon')).toBeVisible();
    expect(screen.queryByText(/This page isn't available/i)).not.toBeInTheDocument();
  });

  it('should display label', async () => {
    const gene = geneExpressionIndividualMocks[0];
    const label = `${gene.gene_symbol} | ${gene.ensembl_gene_id}`;
    await setup();
    expect(screen.getByText(gene.ensembl_gene_id, { exact: false })).toHaveTextContent(label);
  });

  it('should display tissue in header', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 2 })).toHaveTextContent(
      `Individual RNA Expression (${geneExpressionIndividualMocks[0].tissue})`,
    );
  });

  it('should display model name', async () => {
    await setup();
    expect(
      screen.getByText(`${geneExpressionIndividualMocks[0].name} (Females & Males)`),
    ).toBeInTheDocument();
  });

  it('should convert data to CSV and set filename correctly', async () => {
    const mockData: GeneExpressionIndividual[] = [
      {
        ensembl_gene_id: 'ENSMUSG00000001',
        gene_symbol: 'TestGene',
        tissue: 'Brain',
        name: 'TestModel',
        matched_control: 'ControlModel',
        units: 'Log2 Counts',
        result_order: ['ControlModel', 'TestModel'],
        age: '6 months',
        age_numeric: 6,
        data: [
          { genotype: 'ControlModel', sex: 'Female', individual_id: '001', value: 1.23 },
          { genotype: 'TestModel', sex: 'Male', individual_id: '002', value: 4.56 },
        ],
      },
    ];

    const expectedCsvData: string[][] = [
      ['ensembl_gene_id', 'gene_symbol', 'age', 'genotype', 'sex', 'individual_id', 'log2_cpm'],
      ['ENSMUSG00000001', 'TestGene', '6 months', 'ControlModel', 'Female', '001', '1.23'],
      ['ENSMUSG00000001', 'TestGene', '6 months', 'TestModel', 'Male', '002', '4.56'],
    ];

    const { component } = await setup(mockData);
    const instance = component.fixture.componentInstance;

    const csvData = instance.convertToCsvData(mockData);
    const filename = instance.filename();

    expect(csvData).toEqual(expectedCsvData);
    expect(filename).toBe('gene-expression-individual-TestGene-TestModel-brain');
  });
});
