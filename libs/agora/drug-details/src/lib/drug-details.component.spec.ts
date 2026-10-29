import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { Drug, DrugService } from '@sagebionetworks/agora/api-client';
import {
  AGORA_LOADING_ICON_COLORS,
  ROUTE_PARAMS,
  ROUTE_PATHS,
} from '@sagebionetworks/agora/config';
import { drugMock } from '@sagebionetworks/agora/testing';
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
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { Observable, of, throwError } from 'rxjs';
import { DrugDetailsComponent } from './drug-details.component';

async function setup(
  drug = drugMock,
  tab = 'resources',
  subtab = null,
  platformService: Partial<PlatformService> | null = null,
  drugResponse: Observable<Drug> = of(drug),
) {
  const user = userEvent.setup();

  const mockActivatedRoute = {
    paramMap: of(
      convertToParamMap({
        [ROUTE_PARAMS.CHEMBL_ID]: drug.chembl_id,
        [PANEL_ROUTE_PARAMS.TAB]: tab,
        [PANEL_ROUTE_PARAMS.SUBTAB]: subtab,
      }),
    ),
  };

  const mockDrugService = {
    getDrug: jest.fn(() => drugResponse),
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

function drugRequestError(status: HttpStatusCode) {
  return new HttpRequestError(
    new HttpErrorResponse({
      status,
      url: `https://agora.adknowledgeportal.org/api/v1/drugs/${drugMock.chembl_id}`,
    }),
    'GET',
  );
}

async function setupFailedRequest(error: HttpRequestError) {
  const navigateByUrl = jest.spyOn(Router.prototype, 'navigateByUrl').mockResolvedValue(true);
  const showError = jest.spyOn(ErrorOverlayService.prototype, 'showError');
  const loggerError = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
  const loggerWarn = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();
  const rendered = await setup(
    drugMock,
    'summary',
    null,
    null,
    throwError(() => error),
  );
  return { ...rendered, navigateByUrl, showError, loggerError, loggerWarn };
}

describe('DrugDetailsComponent', () => {
  afterEach(() => jest.restoreAllMocks());

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

  it('should redirect to the not found page without reporting when the drug does not exist', async () => {
    const { navigateByUrl, showError, loggerError, loggerWarn } = await setupFailedRequest(
      drugRequestError(HttpStatusCode.NotFound),
    );

    expect(navigateByUrl).toHaveBeenCalledWith(ROUTE_PATHS.NOT_FOUND, {
      skipLocationChange: true,
    });
    expect(showError).not.toHaveBeenCalled();
    expect(loggerError).not.toHaveBeenCalled();
    expect(loggerWarn).not.toHaveBeenCalled();
  });

  it('should show the error overlay and report, without redirecting, when the request fails otherwise', async () => {
    const error = drugRequestError(HttpStatusCode.ServiceUnavailable);

    const { component, navigateByUrl, showError, loggerError } = await setupFailedRequest(error);

    expect(navigateByUrl).not.toHaveBeenCalled();
    expect(showError).toHaveBeenCalledWith(error.userMessage);
    expect(loggerError).toHaveBeenCalledWith(
      formatHttpErrorReportMessage('Error loading drug', error),
      expect.objectContaining({ error }),
    );
    expect(component.container.querySelector('.loading-icon')).not.toBeInTheDocument();
  });
});
