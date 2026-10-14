import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { PlatformService, SUPPRESS_ERROR_OVERLAY } from '@sagebionetworks/explorers/services';
import {
  ModelIdentifierType,
  TranscriptomicsIndividual,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { transcriptomicsIndividualMocks } from '@sagebionetworks/model-ad/testing';
import {
  MODEL_GROUP_QUERY_KEY,
  MODEL_QUERY_KEY,
  TISSUE_QUERY_KEY,
} from '@sagebionetworks/model-ad/util';
import { render, screen } from '@testing-library/angular';
import { Observable, of, switchMap, throwError, timer } from 'rxjs';
import {
  IndividualExpressionDetailsFetchOptions,
  individualExpressionDetailsFromRoute,
  IndividualExpressionDetailsQuery,
} from './individual-expression-details-from-route';

const NOT_FOUND_DELAY_MS = 10;
const PATH_PARAM = 'ensemblGeneId';
const DETAILS_PATH = 'details';

type FetchData = (
  id: string,
  query: IndividualExpressionDetailsQuery,
  options: IndividualExpressionDetailsFetchOptions,
) => Observable<TranscriptomicsIndividual[]>;

let fetchMock: jest.Mock<ReturnType<FetchData>, Parameters<FetchData>>;

@Component({
  selector: 'model-ad-details-host',
  template: `
    @if (details.isLoading()) {
      Loading
    } @else {
      <p>Tissue: {{ details.tissue() }}</p>
      <p>Model: {{ details.modelIdentifier() }}</p>
      <p>Records: {{ details.data()?.length }}</p>
    }
  `,
})
class DetailsHostComponent {
  readonly details = individualExpressionDetailsFromRoute({
    pathParam: PATH_PARAM,
    source: 'DetailsHostComponent',
    fetchData: (id, query, options) => fetchMock(id, query, options),
  });
}

@Component({
  selector: 'model-ad-not-found-stub',
  template: 'Not found',
})
class NotFoundStubComponent {}

describe('individualExpressionDetailsFromRoute', () => {
  const gene = transcriptomicsIndividualMocks[0];
  const otherEnsemblGeneId = 'ENSMUSG00000000001';
  const otherTissue = 'Cortex';
  const modelGroup = 'LOAD2';
  const unknownEnsemblGeneId = 'Unknown';
  const noDataEnsemblGeneId = 'NoData';

  const detailsRoute = (
    id: string,
    queryParams: Record<string, string> = {
      [MODEL_QUERY_KEY]: gene.name,
      [TISSUE_QUERY_KEY]: gene.tissue,
    },
  ) => `/${DETAILS_PATH}/${id}?${new URLSearchParams(queryParams)}`;

  async function setup(initialRoute: string, { notFoundDelayMs = 0, isBrowser = true } = {}) {
    fetchMock = jest.fn<ReturnType<FetchData>, Parameters<FetchData>>((id) => {
      if (id === noDataEnsemblGeneId) return of([]);
      if (id !== unknownEnsemblGeneId) return of(transcriptomicsIndividualMocks);

      const notFound = throwError(() => new Error(`${id} not found`));
      return notFoundDelayMs ? timer(notFoundDelayMs).pipe(switchMap(() => notFound)) : notFound;
    });

    const { navigate } = await render('<router-outlet></router-outlet>', {
      imports: [RouterOutlet],
      routes: [
        { path: `${DETAILS_PATH}/:${PATH_PARAM}`, component: DetailsHostComponent },
        { path: ROUTE_PATHS.NOT_FOUND, component: NotFoundStubComponent },
      ],
      initialRoute,
      providers: [{ provide: PlatformService, useValue: { isBrowser, isServer: !isBrowser } }],
    });

    return { navigate };
  }

  const requests = () => fetchMock.mock.calls.map(([id, query]) => ({ id, query }));

  it('should expose the route info and the fetched data', async () => {
    await setup(detailsRoute(gene.ensembl_gene_id));

    expect(await screen.findByText(`Tissue: ${gene.tissue}`)).toBeInTheDocument();
    expect(screen.getByText(`Model: ${gene.name}`)).toBeInTheDocument();
    expect(
      screen.getByText(`Records: ${transcriptomicsIndividualMocks.length}`),
    ).toBeInTheDocument();
  });

  it('should suppress the error overlay on the request', async () => {
    await setup(detailsRoute(gene.ensembl_gene_id));

    const [, , options] = fetchMock.mock.calls[0];
    expect(options.context.get(SUPPRESS_ERROR_OVERLAY)).toBe(true);
  });

  it('should stay loading without a request on the server', async () => {
    await setup(detailsRoute(gene.ensembl_gene_id), { isBrowser: false });

    expect(screen.getByText('Loading')).toBeInTheDocument();
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it('should issue one request per navigation with the params of that navigation', async () => {
    const { navigate } = await setup(detailsRoute(gene.ensembl_gene_id));

    await navigate(
      detailsRoute(otherEnsemblGeneId, {
        [MODEL_QUERY_KEY]: gene.name,
        [TISSUE_QUERY_KEY]: otherTissue,
      }),
    );

    expect(requests()).toEqual([
      {
        id: gene.ensembl_gene_id,
        query: {
          tissue: gene.tissue,
          modelIdentifierType: ModelIdentifierType.Name,
          modelIdentifier: gene.name,
        },
      },
      {
        id: otherEnsemblGeneId,
        query: {
          tissue: otherTissue,
          modelIdentifierType: ModelIdentifierType.Name,
          modelIdentifier: gene.name,
        },
      },
    ]);
  });

  it('should issue one more request when only the query params change', async () => {
    const { navigate } = await setup(detailsRoute(gene.ensembl_gene_id));

    await navigate(
      detailsRoute(gene.ensembl_gene_id, {
        [MODEL_QUERY_KEY]: gene.name,
        [TISSUE_QUERY_KEY]: otherTissue,
      }),
    );

    expect(requests().map(({ query }) => query.tissue)).toEqual([gene.tissue, otherTissue]);
  });

  it('should request by model group when the url has a model group', async () => {
    await setup(
      detailsRoute(gene.ensembl_gene_id, {
        [MODEL_GROUP_QUERY_KEY]: modelGroup,
        [TISSUE_QUERY_KEY]: gene.tissue,
      }),
    );

    expect(requests()).toEqual([
      {
        id: gene.ensembl_gene_id,
        query: {
          tissue: gene.tissue,
          modelIdentifierType: ModelIdentifierType.ModelGroup,
          modelIdentifier: modelGroup,
        },
      },
    ]);
  });

  it.each([
    ['tissue', { [MODEL_QUERY_KEY]: gene.name }],
    ['model and model group', { [TISSUE_QUERY_KEY]: gene.tissue }],
  ])(
    'should redirect to the not found page without a request when the url is missing %s',
    async (_, queryParams) => {
      await setup(detailsRoute(gene.ensembl_gene_id, queryParams));

      expect(await screen.findByText('Not found')).toBeInTheDocument();
      expect(fetchMock).not.toHaveBeenCalled();
    },
  );

  it('should redirect to the not found page when the request fails', async () => {
    await setup(detailsRoute(unknownEnsemblGeneId));

    expect(await screen.findByText('Not found')).toBeInTheDocument();
  });

  it('should redirect to the not found page when the request returns no data', async () => {
    await setup(detailsRoute(noDataEnsemblGeneId));

    expect(await screen.findByText('Not found')).toBeInTheDocument();
  });

  it('should ignore a failed request that a newer navigation has superseded', async () => {
    const { navigate } = await setup(detailsRoute(unknownEnsemblGeneId), {
      notFoundDelayMs: NOT_FOUND_DELAY_MS,
    });

    await navigate(detailsRoute(gene.ensembl_gene_id));
    await new Promise((resolve) => setTimeout(resolve, NOT_FOUND_DELAY_MS * 2));

    expect(screen.queryByText('Not found')).not.toBeInTheDocument();
  });
});
