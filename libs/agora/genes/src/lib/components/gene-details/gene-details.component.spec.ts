import { provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { Gene, GeneService } from '@sagebionetworks/agora/api-client';
import { ROUTE_PARAMS } from '@sagebionetworks/agora/config';
import { geneMock1, geneMock3 } from '@sagebionetworks/agora/testing';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { of } from 'rxjs';
import { GeneDetailsComponent } from './gene-details.component';

const ALWAYS_AVAILABLE_TABS = [
  'Summary',
  'Evidence',
  'RNA',
  'Protein',
  'Metabolomics',
  'Resources',
];
const DATA_DEPENDENT_TABS = ['Nomination Details', 'Experimental Validation'];

async function setup(gene: Gene = geneMock1) {
  const mockActivatedRoute = {
    paramMap: of(convertToParamMap({ [ROUTE_PARAMS.GENE_ID]: gene.ensembl_gene_id })),
    queryParams: of({}),
  };

  const mockGeneService = {
    getGene: jest.fn(() => of(gene)),
  };

  return render(GeneDetailsComponent, {
    providers: [
      provideHttpClient(),
      { provide: ActivatedRoute, useValue: mockActivatedRoute },
      { provide: GeneService, useValue: mockGeneService },
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });
}

describe('GeneDetailsComponent', () => {
  afterAll(() => jest.restoreAllMocks());

  it('should display the gene symbol', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(geneMock1.hgnc_symbol);
  });

  it('should display the summary panel by default', async () => {
    await setup();
    expect(
      screen.getByRole('heading', { level: 2, name: 'Summary of Evidence' }),
    ).toBeInTheDocument();
  });

  it('should display all tabs for a gene with nominations and experimental validation', async () => {
    await setup(geneMock1);
    for (const tab of [...ALWAYS_AVAILABLE_TABS, ...DATA_DEPENDENT_TABS]) {
      expect(screen.getByText(tab, { exact: true })).toBeInTheDocument();
    }
  });

  it('should hide data-dependent tabs for a gene without nominations or experimental validation', async () => {
    await setup(geneMock3);
    for (const tab of ALWAYS_AVAILABLE_TABS) {
      expect(screen.getByText(tab, { exact: true })).toBeInTheDocument();
    }
    for (const tab of DATA_DEPENDENT_TABS) {
      expect(screen.queryByText(tab, { exact: true })).not.toBeInTheDocument();
    }
  });
});
