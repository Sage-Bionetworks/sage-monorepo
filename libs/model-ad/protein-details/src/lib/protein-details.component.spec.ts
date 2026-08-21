import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { proteomicsIndividualMocks } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { of } from 'rxjs';
import { ProteinDetailsComponent } from './protein-details.component';

async function setup(
  proteinDetails = proteomicsIndividualMocks,
  platformService: Partial<PlatformService> | null = null,
) {
  const user = userEvent.setup();

  const mockActivatedRoute = {
    paramMap: of(
      convertToParamMap({
        uniqueId: proteinDetails[0].unique_id,
      }),
    ),
    queryParamMap: of(
      convertToParamMap({
        model: proteinDetails[0].name,
        tissue: proteinDetails[0].tissue,
      }),
    ),
  };

  const mockPlatformService = platformService || {
    isBrowser: true,
    isServer: false,
  };

  const component = await render(ProteinDetailsComponent, {
    imports: [LoadingIconComponent],
    providers: [
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
});
