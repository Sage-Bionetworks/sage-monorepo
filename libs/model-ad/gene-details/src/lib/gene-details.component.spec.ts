import { Component } from '@angular/core';
import { ActivatedRoute, convertToParamMap, RouterOutlet } from '@angular/router';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import {
  ModelIdentifierType,
  TranscriptomicsIndividualFilterQuery,
  TranscriptomicsIndividualService,
} from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS, ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { transcriptomicsIndividualMocks } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { MessageService } from 'primeng/api';
import { of, switchMap, throwError, timer } from 'rxjs';
import { GeneDetailsComponent } from './gene-details.component';

const NOT_FOUND_DELAY_MS = 10;

@Component({
  selector: 'model-ad-not-found-stub',
  template: 'Not found',
})
class NotFoundStubComponent {}

async function setup(
  geneDetails = transcriptomicsIndividualMocks,
  platformService: Partial<PlatformService> | null = null,
) {
  const user = userEvent.setup();

  const paramMap = convertToParamMap({ ensemblGeneId: geneDetails[0].ensembl_gene_id });
  const queryParamMap = convertToParamMap({
    model: geneDetails[0].name,
    tissue: geneDetails[0].tissue,
  });
  const mockActivatedRoute = {
    paramMap: of(paramMap),
    queryParamMap: of(queryParamMap),
    snapshot: { paramMap, queryParamMap },
  };

  const mockTranscriptomicsIndividualService = {
    getTranscriptomicsIndividual: jest.fn(() => of(geneDetails)),
  };

  const mockPlatformService = platformService || {
    isBrowser: true,
    isServer: false,
  };

  const component = await render(GeneDetailsComponent, {
    imports: [LoadingIconComponent],
    providers: [
      { provide: TranscriptomicsIndividualService, useValue: mockTranscriptomicsIndividualService },
      { provide: PlatformService, useValue: mockPlatformService },
      {
        provide: ActivatedRoute,
        useValue: mockActivatedRoute,
      },
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
      MessageService,
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

    const { component } = await setup(transcriptomicsIndividualMocks, mockPlatformService);
    expect(component.container.querySelector('.loading-icon')).toBeVisible();
    expect(screen.queryByText(/This page isn't available/i)).not.toBeInTheDocument();
  });

  it('should display label', async () => {
    const gene = transcriptomicsIndividualMocks[0];
    const label = `${gene.gene_symbol} | ${gene.ensembl_gene_id}`;
    await setup();
    expect(screen.getByText(gene.ensembl_gene_id, { exact: false })).toHaveTextContent(label);
  });

  it('should display tissue in header', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 2 })).toHaveTextContent(
      `Individual RNA Expression (${transcriptomicsIndividualMocks[0].tissue})`,
    );
  });

  it('should display model name', async () => {
    await setup();
    expect(screen.getByText(`${transcriptomicsIndividualMocks[0].name}`)).toBeInTheDocument();
  });

  describe('navigation', () => {
    const gene = transcriptomicsIndividualMocks[0];
    const otherEnsemblGeneId = 'ENSMUSG00000000001';
    const otherTissue = 'Cortex';
    const modelGroup = 'LOAD2';
    const unknownEnsemblGeneId = 'Unknown';

    const geneRoute = (
      ensemblGeneId: string,
      queryParams: Record<string, string> = { model: gene.name, tissue: gene.tissue },
    ) => `/${ROUTE_PATHS.GENES}/${ensemblGeneId}?${new URLSearchParams(queryParams)}`;

    async function setupWithRouter(initialRoute: string, notFoundDelayMs = 0) {
      const getTranscriptomicsIndividual = jest.fn(
        (query: TranscriptomicsIndividualFilterQuery) => {
          if (query.ensemblGeneId !== unknownEnsemblGeneId)
            return of(transcriptomicsIndividualMocks);

          const notFound = throwError(() => new Error(`${query.ensemblGeneId} not found`));
          return notFoundDelayMs
            ? timer(notFoundDelayMs).pipe(switchMap(() => notFound))
            : notFound;
        },
      );

      const { navigate } = await render('<router-outlet></router-outlet>', {
        imports: [RouterOutlet, LoadingIconComponent],
        routes: [
          { path: `${ROUTE_PATHS.GENES}/:ensemblGeneId`, component: GeneDetailsComponent },
          { path: ROUTE_PATHS.NOT_FOUND, component: NotFoundStubComponent },
        ],
        initialRoute,
        providers: [
          { provide: TranscriptomicsIndividualService, useValue: { getTranscriptomicsIndividual } },
          { provide: PlatformService, useValue: { isBrowser: true, isServer: false } },
          provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
          MessageService,
        ],
      });

      return { navigate, getTranscriptomicsIndividual };
    }

    const requestedQueries = (getTranscriptomicsIndividual: jest.Mock) =>
      getTranscriptomicsIndividual.mock.calls.map(([query]) => query);

    it('should issue one request per navigation with the params of that navigation', async () => {
      const { navigate, getTranscriptomicsIndividual } = await setupWithRouter(
        geneRoute(gene.ensembl_gene_id),
      );

      await navigate(geneRoute(otherEnsemblGeneId, { model: gene.name, tissue: otherTissue }));

      expect(requestedQueries(getTranscriptomicsIndividual)).toEqual([
        {
          ensemblGeneId: gene.ensembl_gene_id,
          tissue: gene.tissue,
          modelIdentifierType: ModelIdentifierType.Name,
          modelIdentifier: gene.name,
        },
        {
          ensemblGeneId: otherEnsemblGeneId,
          tissue: otherTissue,
          modelIdentifierType: ModelIdentifierType.Name,
          modelIdentifier: gene.name,
        },
      ]);
    });

    it('should issue one request when only the query params change', async () => {
      const { navigate, getTranscriptomicsIndividual } = await setupWithRouter(
        geneRoute(gene.ensembl_gene_id),
      );

      await navigate(geneRoute(gene.ensembl_gene_id, { model: gene.name, tissue: otherTissue }));

      expect(requestedQueries(getTranscriptomicsIndividual).map(({ tissue }) => tissue)).toEqual([
        gene.tissue,
        otherTissue,
      ]);
    });

    it('should request by model group when the url has a model group', async () => {
      const { getTranscriptomicsIndividual } = await setupWithRouter(
        geneRoute(gene.ensembl_gene_id, { modelGroup, tissue: gene.tissue }),
      );

      expect(requestedQueries(getTranscriptomicsIndividual)).toEqual([
        {
          ensemblGeneId: gene.ensembl_gene_id,
          tissue: gene.tissue,
          modelIdentifierType: ModelIdentifierType.ModelGroup,
          modelIdentifier: modelGroup,
        },
      ]);
    });

    it.each([
      ['tissue', { model: gene.name }],
      ['model and model group', { tissue: gene.tissue }],
    ])(
      'should redirect to the not found page without a request when the url is missing %s',
      async (_, queryParams) => {
        const { getTranscriptomicsIndividual } = await setupWithRouter(
          geneRoute(gene.ensembl_gene_id, queryParams),
        );

        expect(await screen.findByText('Not found')).toBeInTheDocument();
        expect(getTranscriptomicsIndividual).not.toHaveBeenCalled();
      },
    );

    it('should redirect to the not found page when the request fails', async () => {
      await setupWithRouter(geneRoute(unknownEnsemblGeneId));

      expect(await screen.findByText('Not found')).toBeInTheDocument();
    });

    it('should ignore a failed request that a newer navigation has superseded', async () => {
      const { navigate } = await setupWithRouter(
        geneRoute(unknownEnsemblGeneId),
        NOT_FOUND_DELAY_MS,
      );

      await navigate(geneRoute(gene.ensembl_gene_id));
      await new Promise((resolve) => setTimeout(resolve, NOT_FOUND_DELAY_MS * 2));

      expect(screen.queryByText('Not found')).not.toBeInTheDocument();
    });
  });
});
