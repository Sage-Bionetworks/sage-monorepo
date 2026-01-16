import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { GeneExpressionIndividualService } from '@sagebionetworks/model-ad/api-client';
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
});
