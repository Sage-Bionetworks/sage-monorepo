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

    it('should fetch the marmoset model when modelOrganism is marmoset with wrong casing', async () => {
      const { getModelByName } = await setup(marmosetModelMock, 'omics', null, null, 'Marmoset');
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
    it('should delegate rendering to the mouse content component', async () => {
      const { component } = await setup();
      expect(
        component.container.querySelector('model-ad-mouse-model-details-content'),
      ).toBeInTheDocument();
      expect(
        component.container.querySelector('model-ad-marmoset-model-details-content'),
      ).not.toBeInTheDocument();
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
    it('should delegate rendering to the marmoset content component', async () => {
      const { component } = await setup(marmosetModelMock, 'biomarkers', null, null, 'marmoset');
      expect(
        component.container.querySelector('model-ad-marmoset-model-details-content'),
      ).toBeInTheDocument();
      expect(
        component.container.querySelector('model-ad-mouse-model-details-content'),
      ).not.toBeInTheDocument();
    });
  });
});
