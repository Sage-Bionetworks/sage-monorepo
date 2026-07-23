import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { Model, ModelOrganism, ModelService } from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { marmosetModelMock, mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { of } from 'rxjs';
import { ModelDetailsComponent } from './model-details.component';

async function setup(
  model: Model = mouseModelMock,
  tab = 'omics',
  subtab = null,
  platformService: Partial<PlatformService> | null = null,
  modelOrganism: string | null = null,
) {
  const user = userEvent.setup();

  const queryParams = modelOrganism === null ? {} : { modelOrganism };
  const mockActivatedRoute = {
    paramMap: of(convertToParamMap({ name: model.name, tab: tab, subtab: subtab })),
    queryParamMap: of(convertToParamMap(queryParams)),
  };

  const mockModelService = {
    getModelByName: jest.fn(() => of(model)),
  };

  const mockPlatformService = platformService || {
    isBrowser: true,
    isServer: false,
  };

  const component = await render(ModelDetailsComponent, {
    imports: [LoadingIconComponent],
    providers: [
      { provide: ModelService, useValue: mockModelService },
      { provide: PlatformService, useValue: mockPlatformService },
      {
        provide: ActivatedRoute,
        useValue: mockActivatedRoute,
      },
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
    ],
  });

  return { user, component, getModelByName: mockModelService.getModelByName };
}

describe('ModelDetailsComponent', () => {
  afterAll(() => jest.restoreAllMocks());

  describe('model organism resolution', () => {
    it('should fetch the mouse model when no modelOrganism query param is provided', async () => {
      const { getModelByName } = await setup();
      expect(getModelByName).toHaveBeenCalledWith(
        ModelOrganism.Mouse,
        mouseModelMock.name,
        'body',
        false,
        expect.anything(),
      );
    });

    it('should fetch the mouse model when modelOrganism is mouse', async () => {
      const { getModelByName } = await setup(mouseModelMock, 'omics', null, null, 'mouse');
      expect(getModelByName).toHaveBeenCalledWith(
        ModelOrganism.Mouse,
        mouseModelMock.name,
        'body',
        false,
        expect.anything(),
      );
    });

    it('should fetch the marmoset model when modelOrganism is marmoset', async () => {
      const { getModelByName } = await setup(marmosetModelMock, 'omics', null, null, 'marmoset');
      expect(getModelByName).toHaveBeenCalledWith(
        ModelOrganism.Marmoset,
        marmosetModelMock.name,
        'body',
        false,
        expect.anything(),
      );
    });
  });

  describe('mouse model', () => {
    it('should display the model name', async () => {
      await setup();
      expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(mouseModelMock.name);
    });

    it('should display all tabs for which the model has data', async () => {
      await setup();
      const tabs = ['Omics', 'Biomarkers', 'Pathology', 'Resources'];
      for (const tab of tabs) {
        expect(screen.getByText(tab)).toBeInTheDocument();
      }
    });

    it('should hide tabs for which the model does not have data', async () => {
      const modelWithoutOmics = { ...mouseModelMock, biomarkers: [], pathology: [] };
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

      const { component } = await setup(mouseModelMock, 'omics', null, mockPlatformService);
      expect(component.container.querySelector('.loading-icon')).toBeVisible();
      expect(screen.queryByText(/This page isn't available/i)).not.toBeInTheDocument();
    });
  });

  describe('marmoset model', () => {
    it('should render only the model name and no mouse panels for a marmoset model', async () => {
      await setup(marmosetModelMock, 'omics', null, null, 'marmoset');
      expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(marmosetModelMock.name);
      expect(screen.queryByText('Omics')).not.toBeInTheDocument();
      expect(screen.queryByText('Biomarkers')).not.toBeInTheDocument();
      expect(screen.queryByText('Pathology')).not.toBeInTheDocument();
      expect(screen.queryByText('Resources')).not.toBeInTheDocument();
    });
  });
});
