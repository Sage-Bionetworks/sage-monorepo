import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { ModelsService } from '@sagebionetworks/model-ad/api-client-angular';
import { ConfigService, MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { configMock, modelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { of } from 'rxjs';
import { ModelDetailsComponent } from './model-details.component';

async function setup(model = modelMock, tab = 'omics', subtab = null, config = configMock) {
  const user = userEvent.setup();

  const mockActivatedRoute = {
    paramMap: of(convertToParamMap({ model: model.model, tab: tab, subtab: subtab })),
  };

  const mockModelsService = {
    getModelByName: jest.fn(() => of(model)),
  };

  const configServiceMock = {
    config: config,
  };

  const component = await render(ModelDetailsComponent, {
    imports: [LoadingIconComponent],
    providers: [
      { provide: ModelsService, useValue: mockModelsService },
      {
        provide: ActivatedRoute,
        useValue: mockActivatedRoute,
      },
      { provide: ConfigService, useValue: configServiceMock },
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
    ],
  });

  return { user, component };
}

describe('ModelDetailsComponent', () => {
  afterAll(() => jest.restoreAllMocks());

  it('should display the model name', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(modelMock.model);
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
    const configServer = { ...configMock, isPlatformServer: true };
    await setup(modelMock, 'omics', null, configServer);
    expect(document.querySelector('.loading-icon')).toBeVisible();
    expect(screen.queryByText(/This page isn't available/i)).not.toBeInTheDocument();
  });
});
