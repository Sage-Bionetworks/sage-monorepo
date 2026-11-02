import { HttpErrorResponse, HttpStatusCode, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { Gene, GeneService } from '@sagebionetworks/agora/api-client';
import {
  AGORA_LOADING_ICON_COLORS,
  ROUTE_PARAMS,
  ROUTE_PATHS,
} from '@sagebionetworks/agora/config';
import { geneMock1, geneMock3 } from '@sagebionetworks/agora/testing';
import {
  formatHttpErrorReportMessage,
  ErrorOverlayService,
  HttpRequestError,
  PlatformService,
  SourceLogger,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors, SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { Observable, of, throwError } from 'rxjs';
import { GENE_ERROR_MESSAGE, GeneDetailsComponent } from './gene-details.component';

const ALWAYS_AVAILABLE_TABS = [
  'Summary',
  'Evidence',
  'RNA',
  'Protein',
  'Metabolomics',
  'Resources',
];
const DATA_DEPENDENT_TABS = ['Nomination Details', 'Experimental Validation'];

const browserPlatform: Partial<PlatformService> = { isBrowser: true, isServer: false };
const serverPlatform: Partial<PlatformService> = { isBrowser: false, isServer: true };

async function setup({
  gene = geneMock1,
  geneResponse = of(gene),
  platformService = browserPlatform,
}: {
  gene?: Gene;
  geneResponse?: Observable<Gene | null>;
  platformService?: Partial<PlatformService>;
} = {}) {
  const navigateByUrl = jest.spyOn(Router.prototype, 'navigateByUrl').mockResolvedValue(true);

  const mockActivatedRoute = {
    paramMap: of(convertToParamMap({ [ROUTE_PARAMS.GENE_ID]: gene.ensembl_gene_id })),
    queryParams: of({}),
  };

  const mockGeneService = {
    getGene: jest.fn(() => geneResponse),
  };

  const component = await render(GeneDetailsComponent, {
    providers: [
      provideHttpClient(),
      { provide: ActivatedRoute, useValue: mockActivatedRoute },
      { provide: GeneService, useValue: mockGeneService },
      { provide: PlatformService, useValue: platformService },
      { provide: SvgIconService, useClass: SvgIconServiceStub },
      provideLoadingIconColors(AGORA_LOADING_ICON_COLORS),
    ],
  });

  return { component, navigateByUrl, mockGeneService };
}

describe('GeneDetailsComponent', () => {
  afterEach(() => jest.restoreAllMocks());

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
    await setup({ gene: geneMock1 });
    for (const tab of [...ALWAYS_AVAILABLE_TABS, ...DATA_DEPENDENT_TABS]) {
      expect(screen.getByText(tab, { exact: true })).toBeInTheDocument();
    }
  });

  it('should hide data-dependent tabs for a gene without nominations or experimental validation', async () => {
    await setup({ gene: geneMock3 });
    for (const tab of ALWAYS_AVAILABLE_TABS) {
      expect(screen.getByText(tab, { exact: true })).toBeInTheDocument();
    }
    for (const tab of DATA_DEPENDENT_TABS) {
      expect(screen.queryByText(tab, { exact: true })).not.toBeInTheDocument();
    }
  });

  it('should show the loading icon and not fetch the gene on the server', async () => {
    const { component, mockGeneService } = await setup({ platformService: serverPlatform });
    expect(component.container.querySelector('.loading-icon')).toBeVisible();
    expect(mockGeneService.getGene).not.toHaveBeenCalled();
  });

  it('should redirect to the not found page when the gene does not exist', async () => {
    const { navigateByUrl } = await setup({ geneResponse: of(null) });
    expect(navigateByUrl).toHaveBeenCalledWith(ROUTE_PATHS.NOT_FOUND, {
      skipLocationChange: true,
    });
  });

  it('should leave the loading state, report, and show the error overlay, without redirecting, when the request fails', async () => {
    const error = new HttpRequestError(
      new HttpErrorResponse({
        status: HttpStatusCode.ServiceUnavailable,
        url: `https://agora.adknowledgeportal.org/api/v1/genes/${geneMock1.ensembl_gene_id}`,
      }),
      'GET',
    );
    const loggerError = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
    const showRequestError = jest.spyOn(ErrorOverlayService.prototype, 'showRequestError');

    const { component, navigateByUrl } = await setup({ geneResponse: throwError(() => error) });

    expect(showRequestError).toHaveBeenCalledWith(error);

    expect(navigateByUrl).not.toHaveBeenCalled();
    expect(component.container.querySelector('.loading-icon')).not.toBeInTheDocument();
    expect(loggerError).toHaveBeenCalledWith(
      formatHttpErrorReportMessage(GENE_ERROR_MESSAGE, error),
      expect.objectContaining({ error }),
    );
  });
});
