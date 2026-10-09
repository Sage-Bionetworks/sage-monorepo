import { Component } from '@angular/core';
import { ActivatedRoute, convertToParamMap, RouterOutlet } from '@angular/router';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import {
  ModelIdentifierType,
  ProteomicsIndividualFilterQuery,
  ProteomicsIndividualService,
} from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS, ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { proteomicsIndividualMocks } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { MessageService } from 'primeng/api';
import { of, switchMap, throwError, timer } from 'rxjs';
import { ProteinDetailsComponent } from './protein-details.component';

const NOT_FOUND_DELAY_MS = 10;

@Component({
  selector: 'model-ad-not-found-stub',
  template: 'Not found',
})
class NotFoundStubComponent {}

async function setup(
  proteinDetails = proteomicsIndividualMocks,
  platformService: Partial<PlatformService> | null = null,
) {
  const user = userEvent.setup();

  const paramMap = convertToParamMap({ uniqueId: proteinDetails[0].unique_id });
  const queryParamMap = convertToParamMap({
    model: proteinDetails[0].name,
    tissue: proteinDetails[0].tissue,
  });
  const mockActivatedRoute = {
    paramMap: of(paramMap),
    queryParamMap: of(queryParamMap),
    snapshot: { paramMap, queryParamMap },
  };

  const mockProteomicsIndividualService = {
    getProteomicsIndividual: jest.fn(() => of(proteinDetails)),
  };

  const mockPlatformService = platformService || {
    isBrowser: true,
    isServer: false,
  };

  const component = await render(ProteinDetailsComponent, {
    imports: [LoadingIconComponent],
    providers: [
      { provide: ProteomicsIndividualService, useValue: mockProteomicsIndividualService },
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

describe('ProteinDetailsComponent', () => {
  afterAll(() => jest.restoreAllMocks());

  it('should show loading icon on server', async () => {
    const mockPlatformService = {
      isBrowser: false,
      isServer: true,
    };

    const { component } = await setup(proteomicsIndividualMocks, mockPlatformService);
    expect(component.container.querySelector('.loading-icon')).toBeVisible();
    expect(screen.queryByText(/This page isn't available/i)).not.toBeInTheDocument();
  });

  it('should display label', async () => {
    const protein = proteomicsIndividualMocks[0];
    const label = `${protein.display_symbol} | ${protein.ensembl_gene_id}`;
    await setup();
    expect(screen.getByText(protein.ensembl_gene_id, { exact: false })).toHaveTextContent(label);
  });

  it('should display tissue in header', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 2 })).toHaveTextContent(
      `Individual Protein Expression (${proteomicsIndividualMocks[0].tissue})`,
    );
  });

  it('should display model name', async () => {
    await setup();
    expect(screen.getByText(`${proteomicsIndividualMocks[0].name}`)).toBeInTheDocument();
  });

  describe('navigation', () => {
    const protein = proteomicsIndividualMocks[0];
    const otherUniqueId = 'ENSMUSG00000000001A0A000';
    const otherTissue = 'Cortex';
    const modelGroup = 'LOAD1';
    const unknownUniqueId = 'Unknown';

    const proteinRoute = (
      uniqueId: string,
      queryParams: Record<string, string> = { model: protein.name, tissue: protein.tissue },
    ) => `/${ROUTE_PATHS.PROTEINS}/${uniqueId}?${new URLSearchParams(queryParams)}`;

    async function setupWithRouter(initialRoute: string, notFoundDelayMs = 0) {
      const getProteomicsIndividual = jest.fn((query: ProteomicsIndividualFilterQuery) => {
        if (query.uniqueId !== unknownUniqueId) return of(proteomicsIndividualMocks);

        const notFound = throwError(() => new Error(`${query.uniqueId} not found`));
        return notFoundDelayMs ? timer(notFoundDelayMs).pipe(switchMap(() => notFound)) : notFound;
      });

      const { navigate } = await render('<router-outlet></router-outlet>', {
        imports: [RouterOutlet, LoadingIconComponent],
        routes: [
          { path: `${ROUTE_PATHS.PROTEINS}/:uniqueId`, component: ProteinDetailsComponent },
          { path: ROUTE_PATHS.NOT_FOUND, component: NotFoundStubComponent },
        ],
        initialRoute,
        providers: [
          { provide: ProteomicsIndividualService, useValue: { getProteomicsIndividual } },
          { provide: PlatformService, useValue: { isBrowser: true, isServer: false } },
          provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
          MessageService,
        ],
      });

      return { navigate, getProteomicsIndividual };
    }

    const requestedQueries = (getProteomicsIndividual: jest.Mock) =>
      getProteomicsIndividual.mock.calls.map(([query]) => query);

    it('should issue one request per navigation with the params of that navigation', async () => {
      const { navigate, getProteomicsIndividual } = await setupWithRouter(
        proteinRoute(protein.unique_id),
      );

      await navigate(proteinRoute(otherUniqueId, { model: protein.name, tissue: otherTissue }));

      expect(requestedQueries(getProteomicsIndividual)).toEqual([
        {
          uniqueId: protein.unique_id,
          tissue: protein.tissue,
          modelIdentifierType: ModelIdentifierType.Name,
          modelIdentifier: protein.name,
        },
        {
          uniqueId: otherUniqueId,
          tissue: otherTissue,
          modelIdentifierType: ModelIdentifierType.Name,
          modelIdentifier: protein.name,
        },
      ]);
    });

    it('should issue one request when only the query params change', async () => {
      const { navigate, getProteomicsIndividual } = await setupWithRouter(
        proteinRoute(protein.unique_id),
      );

      await navigate(proteinRoute(protein.unique_id, { model: protein.name, tissue: otherTissue }));

      expect(requestedQueries(getProteomicsIndividual).map(({ tissue }) => tissue)).toEqual([
        protein.tissue,
        otherTissue,
      ]);
    });

    it('should request by model group when the url has a model group', async () => {
      const { getProteomicsIndividual } = await setupWithRouter(
        proteinRoute(protein.unique_id, { modelGroup, tissue: protein.tissue }),
      );

      expect(requestedQueries(getProteomicsIndividual)).toEqual([
        {
          uniqueId: protein.unique_id,
          tissue: protein.tissue,
          modelIdentifierType: ModelIdentifierType.ModelGroup,
          modelIdentifier: modelGroup,
        },
      ]);
    });

    it.each([
      ['tissue', { model: protein.name }],
      ['model and model group', { tissue: protein.tissue }],
    ])(
      'should redirect to the not found page without a request when the url is missing %s',
      async (_, queryParams) => {
        const { getProteomicsIndividual } = await setupWithRouter(
          proteinRoute(protein.unique_id, queryParams),
        );

        expect(await screen.findByText('Not found')).toBeInTheDocument();
        expect(getProteomicsIndividual).not.toHaveBeenCalled();
      },
    );

    it('should redirect to the not found page when the request fails', async () => {
      await setupWithRouter(proteinRoute(unknownUniqueId));

      expect(await screen.findByText('Not found')).toBeInTheDocument();
    });

    it('should ignore a failed request that a newer navigation has superseded', async () => {
      const { navigate } = await setupWithRouter(proteinRoute(unknownUniqueId), NOT_FOUND_DELAY_MS);

      await navigate(proteinRoute(protein.unique_id));
      await new Promise((resolve) => setTimeout(resolve, NOT_FOUND_DELAY_MS * 2));

      expect(screen.queryByText('Not found')).not.toBeInTheDocument();
    });
  });
});
