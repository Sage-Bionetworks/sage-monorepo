import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { DrugService } from '@sagebionetworks/agora/api-client';
import { AGORA_LOADING_ICON_COLORS } from '@sagebionetworks/agora/config';
import { drugMock } from '@sagebionetworks/agora/testing';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { of } from 'rxjs';
import { DrugDetailsComponent } from './drug-details.component';

async function setup(
  drug = drugMock,
  tab = 'resources',
  subtab = null,
  platformService: Partial<PlatformService> | null = null,
) {
  const user = userEvent.setup();

  const mockActivatedRoute = {
    paramMap: of(convertToParamMap({ chemblId: drug.chembl_id, tab: tab, subtab: subtab })),
  };

  const mockDrugService = {
    getDrug: jest.fn(() => of(drug)),
  };

  const mockPlatformService = platformService || {
    isBrowser: true,
    isServer: false,
  };

  const component = await render(DrugDetailsComponent, {
    imports: [LoadingIconComponent],
    providers: [
      { provide: DrugService, useValue: mockDrugService },
      { provide: PlatformService, useValue: mockPlatformService },
      {
        provide: ActivatedRoute,
        useValue: mockActivatedRoute,
      },
      provideLoadingIconColors(AGORA_LOADING_ICON_COLORS),
    ],
  });

  return { user, component };
}

describe('DrugDetailsComponent', () => {
  afterAll(() => jest.restoreAllMocks());

  it('should display the drug name', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(drugMock.common_name);
  });

  it('should display all tabs for which the drug has data', async () => {
    await setup();
    const tabs = ['Summary', 'Resources', 'Nomination Details'];
    for (const tab of tabs) {
      expect(screen.getByText(tab)).toBeInTheDocument();
    }
  });

  it('should show loading icon on server', async () => {
    const mockPlatformService = {
      isBrowser: false,
      isServer: true,
    };

    const { component } = await setup(drugMock, 'summary', null, mockPlatformService);
    expect(component.container.querySelector('.loading-icon')).toBeVisible();
    expect(screen.queryByText(/This page isn't available/i)).not.toBeInTheDocument();
  });
});
