import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { ModelsService } from '@sagebionetworks/model-ad/api-client-angular';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { modelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { of } from 'rxjs';
import { ModelDetailsComponent } from './model-details.component';

async function setup(
  model = modelMock,
  tab = 'omics',
  subtab = null,
  platformService: Partial<PlatformService> | null = null,
) {
  const user = userEvent.setup();

  const mockActivatedRoute = {
    paramMap: of(convertToParamMap({ name: model.name, tab: tab, subtab: subtab })),
  };

  const mockModelsService = {
    getModelByName: jest.fn(() => of(model)),
  };

  const mockPlatformService = platformService || {
    isBrowser: true,
    isServer: false,
  };

  const component = await render(ModelDetailsComponent, {
    imports: [LoadingIconComponent],
    providers: [
      { provide: ModelsService, useValue: mockModelsService },
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

describe('ModelDetailsComponent', () => {
  afterAll(() => jest.restoreAllMocks());

  it('should display the model name', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(modelMock.name);
  });

  it('should display all tabs for which the model has data', async () => {
    await setup();
    const tabs = ['Omics', 'Biomarkers', 'Pathology', 'Resources'];
    for (const tab of tabs) {
      expect(screen.getByText(tab)).toBeInTheDocument();
    }
  });

  it('should hide tabs for which the model does not have data', async () => {
    const modelWithoutOmics = { ...modelMock, biomarkers: [], pathology: [] };
    await setup(modelWithoutOmics);
    expect(screen.getByText('Omics')).toBeInTheDocument();
    expect(screen.queryByText('Biomarkers')).not.toBeInTheDocument();
    expect(screen.queryByText('Pathology')).not.toBeInTheDocument();
    expect(screen.getByText('Resources')).toBeInTheDocument();
  });

  it('should show loading icon on server', async () => {
    const mockPlatformService = {
      isBrowser: false,
      isServer: true,
    };

    const { component } = await setup(modelMock, 'omics', null, mockPlatformService);
    expect(component.container.querySelector('.loading-icon')).toBeVisible();
    expect(screen.queryByText(/This page isn't available/i)).not.toBeInTheDocument();
  });
});
