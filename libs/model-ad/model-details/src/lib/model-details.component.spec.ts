import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router, RouterOutlet } from '@angular/router';
import { PANEL_ROUTE_PARAMS } from '@sagebionetworks/explorers/constants';
import {
  formatHttpErrorReportMessage,
  ErrorOverlayService,
  HttpRequestError,
  PlatformService,
  SourceLogger,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { Model, ModelOrganism, ModelService } from '@sagebionetworks/model-ad/api-client';
import {
  MODEL_AD_LOADING_ICON_COLORS,
  ROUTE_PARAMS,
  ROUTE_PATHS,
} from '@sagebionetworks/model-ad/config';
import { marmosetModelMock, mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { MessageService } from 'primeng/api';
import { of, switchMap, throwError, timer } from 'rxjs';
import { ModelDetailsComponent } from './model-details.component';

const NOT_FOUND_DELAY_MS = 10;

@Component({
  selector: 'model-ad-not-found-stub',
  template: 'Not found',
})
class NotFoundStubComponent {}

async function setup(
  model: Model = mouseModelMock,
  tab = 'omics',
  subtab = null,
  platformService: Partial<PlatformService> | null = null,
  modelOrganism: string | null = null,
) {
  const user = userEvent.setup();

  const paramMap = convertToParamMap({
    [ROUTE_PARAMS.MODEL_NAME]: model.name,
    [PANEL_ROUTE_PARAMS.TAB]: tab,
    [PANEL_ROUTE_PARAMS.SUBTAB]: subtab,
  });
  const queryParamMap = convertToParamMap(modelOrganism === null ? {} : { modelOrganism });
  const mockActivatedRoute = {
    paramMap: of(paramMap),
    queryParamMap: of(queryParamMap),
    snapshot: { paramMap, queryParamMap },
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
      MessageService,
    ],
  });

  return { user, component, getModelByName: mockModelService.getModelByName };
}

describe('ModelDetailsComponent', () => {
  afterEach(() => jest.restoreAllMocks());

  describe('model organism resolution', () => {
    it('should fetch the mouse model when no modelOrganism query param is provided', async () => {
      const { getModelByName } = await setup();
      expect(getModelByName).toHaveBeenCalledWith(ModelOrganism.Mouse, mouseModelMock.name);
    });

    it('should fetch the mouse model when modelOrganism is mouse', async () => {
      const { getModelByName } = await setup(mouseModelMock, 'omics', null, null, 'mouse');
      expect(getModelByName).toHaveBeenCalledWith(ModelOrganism.Mouse, mouseModelMock.name);
    });

    it('should fetch the marmoset model when modelOrganism is marmoset', async () => {
      const { getModelByName } = await setup(marmosetModelMock, 'omics', null, null, 'marmoset');
      expect(getModelByName).toHaveBeenCalledWith(ModelOrganism.Marmoset, marmosetModelMock.name);
    });

    it('should fetch the marmoset model when modelOrganism is marmoset with wrong casing', async () => {
      const { getModelByName } = await setup(marmosetModelMock, 'omics', null, null, 'Marmoset');
      expect(getModelByName).toHaveBeenCalledWith(ModelOrganism.Marmoset, marmosetModelMock.name);
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

  describe('navigation between organisms', () => {
    const load1Mock = { ...mouseModelMock, name: 'LOAD1' };
    const load1Route = `/${ROUTE_PATHS.MODELS}/${load1Mock.name}?modelOrganism=mouse`;
    const unknownModelRoute = `/${ROUTE_PATHS.MODELS}/Unknown?modelOrganism=mouse`;
    const initialModelRoute = `/${ROUTE_PATHS.MODELS}/${marmosetModelMock.name}?modelOrganism=marmoset`;
    const unavailableModelName = 'Unavailable';
    const unavailableModelRoute = `/${ROUTE_PATHS.MODELS}/${unavailableModelName}?modelOrganism=mouse`;

    const modelRequestError = (status: HttpStatusCode, modelOrganism: string, name: string) =>
      new HttpRequestError(
        new HttpErrorResponse({
          status,
          url: `https://modeladexplorer.org/api/v1/models/${modelOrganism}/${name}`,
        }),
        'GET',
      );

    async function setupWithRouter(notFoundDelayMs = 0) {
      const getModelByName = jest.fn((modelOrganism: ModelOrganism, name: string) => {
        const model = [marmosetModelMock, load1Mock].find(
          (candidate) => candidate.type === modelOrganism && candidate.name === name,
        );
        if (model) return of(model);

        if (name === unavailableModelName) {
          return throwError(() =>
            modelRequestError(HttpStatusCode.ServiceUnavailable, modelOrganism, name),
          );
        }

        const notFound = throwError(() =>
          modelRequestError(HttpStatusCode.NotFound, modelOrganism, name),
        );
        return notFoundDelayMs ? timer(notFoundDelayMs).pipe(switchMap(() => notFound)) : notFound;
      });

      const { navigate } = await render('<router-outlet></router-outlet>', {
        imports: [RouterOutlet, LoadingIconComponent],
        routes: [
          {
            path: `${ROUTE_PATHS.MODELS}/:${ROUTE_PARAMS.MODEL_NAME}`,
            component: ModelDetailsComponent,
          },
          { path: ROUTE_PATHS.NOT_FOUND, component: NotFoundStubComponent },
        ],
        initialRoute: initialModelRoute,
        providers: [
          { provide: ModelService, useValue: { getModelByName } },
          { provide: PlatformService, useValue: { isBrowser: true, isServer: false } },
          provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
          MessageService,
        ],
      });

      return { navigate, getModelByName, router: TestBed.inject(Router) };
    }

    it('should never request the name of one navigation with the organism of another', async () => {
      const { navigate, getModelByName } = await setupWithRouter();

      await navigate(load1Route);

      expect(
        getModelByName.mock.calls.map(([modelOrganism, name]) => [modelOrganism, name]),
      ).toEqual([
        [ModelOrganism.Marmoset, marmosetModelMock.name],
        [ModelOrganism.Mouse, load1Mock.name],
      ]);
    });

    it('should stay on the requested model page instead of the not found page', async () => {
      const { navigate, router } = await setupWithRouter();

      await navigate(load1Route);

      expect(router.url).toBe(load1Route);
      expect(screen.queryByText('Not found')).not.toBeInTheDocument();
    });

    it('should ignore a failed request that a newer navigation has superseded', async () => {
      const { navigate, router } = await setupWithRouter(NOT_FOUND_DELAY_MS);

      await navigate(unknownModelRoute);
      await navigate(load1Route);
      await new Promise((resolve) => setTimeout(resolve, NOT_FOUND_DELAY_MS * 2));

      expect(router.url).toBe(load1Route);
      expect(screen.queryByText('Not found')).not.toBeInTheDocument();
    });

    it('should redirect to the not found page when the model does not exist', async () => {
      const { navigate } = await setupWithRouter();

      await navigate(unknownModelRoute);

      expect(screen.getByText('Not found')).toBeInTheDocument();
    });

    it('should report a data-quality warning when a link inside the app leads to a missing model', async () => {
      const loggerWarn = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();
      const { navigate } = await setupWithRouter();

      await navigate(unknownModelRoute);

      expect(loggerWarn).toHaveBeenCalledWith('Linked model not found', {
        modelName: 'Unknown',
        modelOrganism: ModelOrganism.Mouse,
        sourcePage: initialModelRoute,
      });
    });

    it('should show the error overlay and report, without redirecting, when the request fails otherwise', async () => {
      const showError = jest.spyOn(ErrorOverlayService.prototype, 'showError');
      const loggerError = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
      const { navigate, router } = await setupWithRouter();
      const error = modelRequestError(
        HttpStatusCode.ServiceUnavailable,
        ModelOrganism.Mouse,
        unavailableModelName,
      );

      await navigate(unavailableModelRoute);

      expect(router.url).toBe(unavailableModelRoute);
      expect(screen.queryByText('Not found')).not.toBeInTheDocument();
      expect(showError).toHaveBeenCalledWith(error.userMessage);
      expect(loggerError).toHaveBeenCalledWith(
        formatHttpErrorReportMessage('Error loading model', error),
        expect.objectContaining({ error: expect.any(HttpRequestError) }),
      );
      expect(document.querySelector('.loading-icon')).not.toBeInTheDocument();
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
