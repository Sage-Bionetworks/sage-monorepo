import { DOCUMENT } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { NOT_FOUND_ROUTE_PATH } from '@sagebionetworks/explorers/constants';
import { DefaultUrlSerializer, Navigation, Router } from '@angular/router';
import * as Sentry from '@sentry/angular';
import { SourceLogger } from './logger.service';
import {
  getNavigationArrival,
  NAVIGATION_ARRIVAL_TAG,
  NavigationArrival,
  NavigationArrivalEnum,
  NotFoundService,
} from './not-found.service';

const SITE_ORIGIN = 'https://agora.adknowledgeportal.org';
const SOURCE_PAGE = '/genes/nominated-targets?pinned=ENSG00000130203';
const SITE_REFERRER = `${SITE_ORIGIN}${SOURCE_PAGE}`;
const MISSING_DRUG = { chemblId: 'CHEMBL0000000' };
const urlSerializer = new DefaultUrlSerializer();

function navigationTo(
  url: string,
  options: { from?: string; trigger?: Navigation['trigger'] } = {},
): Navigation {
  const previousNavigation = options.from
    ? ({
        finalUrl: urlSerializer.parse(options.from),
        previousNavigation: null,
      } as unknown as Navigation)
    : null;
  return {
    finalUrl: urlSerializer.parse(url),
    trigger: options.trigger ?? 'imperative',
    previousNavigation,
  } as unknown as Navigation;
}

describe('getNavigationArrival', () => {
  const drugPage = '/drugs/CHEMBL0000000';

  it('is in-app when the page was reached by a navigation inside the app', () => {
    expect(
      getNavigationArrival(navigationTo(drugPage, { from: SOURCE_PAGE }), '', SITE_ORIGIN),
    ).toBe(NavigationArrivalEnum.InApp);
  });

  it('is history when the page was reached with the back or forward button', () => {
    const navigation = navigationTo(drugPage, { from: SOURCE_PAGE, trigger: 'popstate' });

    expect(getNavigationArrival(navigation, '', SITE_ORIGIN)).toBe(NavigationArrivalEnum.History);
  });

  it("is an internal new tab when the app's first page was opened from one of the site's links", () => {
    expect(getNavigationArrival(navigationTo(drugPage), SITE_REFERRER, SITE_ORIGIN)).toBe(
      NavigationArrivalEnum.InternalNewTab,
    );
  });

  it("is external when the app's first page was opened from another site", () => {
    expect(
      getNavigationArrival(navigationTo(drugPage), 'https://www.google.com/', SITE_ORIGIN),
    ).toBe(NavigationArrivalEnum.External);
  });

  it("is direct when the app's first page has no referrer, such as a typed URL", () => {
    expect(getNavigationArrival(navigationTo(drugPage), '', SITE_ORIGIN)).toBe(
      NavigationArrivalEnum.Direct,
    );
  });

  it("is an internal new tab when the browser loaded the app's first page by following the site's link", () => {
    expect(
      getNavigationArrival(navigationTo(drugPage), SITE_REFERRER, SITE_ORIGIN, 'navigate'),
    ).toBe(NavigationArrivalEnum.InternalNewTab);
  });

  it("is reload when the app's first page was reloaded, though the referrer is the site's own page", () => {
    expect(getNavigationArrival(navigationTo(drugPage), SITE_REFERRER, SITE_ORIGIN, 'reload')).toBe(
      NavigationArrivalEnum.Reload,
    );
  });

  it("is history when the app's first page was loaded with the back or forward button, though the referrer is the site's own page", () => {
    expect(
      getNavigationArrival(navigationTo(drugPage), SITE_REFERRER, SITE_ORIGIN, 'back_forward'),
    ).toBe(NavigationArrivalEnum.History);
  });

  it('is in-app after the first page, however the browser loaded the document', () => {
    const navigation = navigationTo(drugPage, { from: SOURCE_PAGE });

    expect(getNavigationArrival(navigation, SITE_REFERRER, SITE_ORIGIN, 'reload')).toBe(
      NavigationArrivalEnum.InApp,
    );
  });
});

describe('NotFoundService', () => {
  let logger: jest.Mocked<Pick<SourceLogger, 'log' | 'warn'>>;
  let navigateByUrl: jest.Mock;

  function setup(
    navigation: Navigation,
    referrer = '',
    inFlightNavigation: Navigation | null = null,
    documentLoadType?: NavigationTimingType,
  ) {
    const defaultView = documentLoadType
      ? { performance: { getEntriesByType: () => [{ type: documentLoadType }] } }
      : undefined;
    logger = { log: jest.fn(), warn: jest.fn() };
    navigateByUrl = jest.fn(() => Promise.resolve(true));
    TestBed.configureTestingModule({
      providers: [
        {
          provide: Router,
          useValue: {
            currentNavigation: () => inFlightNavigation,
            lastSuccessfulNavigation: navigation,
            navigateByUrl,
            serializeUrl: (url: Parameters<DefaultUrlSerializer['serialize']>[0]) =>
              urlSerializer.serialize(url),
          },
        },
        {
          provide: DOCUMENT,
          useValue: { referrer, location: { origin: SITE_ORIGIN }, defaultView },
        },
      ],
    });
    return TestBed.inject(NotFoundService);
  }

  function redirectToNotFound(notFoundService: NotFoundService) {
    notFoundService.redirectToNotFound(logger as unknown as SourceLogger, 'drug', MISSING_DRUG);
  }

  afterEach(() => jest.restoreAllMocks());

  it('reports a warning with the source page when an in-app link led to a missing item', () => {
    const setTag = jest.spyOn(Sentry.Scope.prototype, 'setTag');
    const notFoundService = setup(navigationTo('/drugs/CHEMBL0000000', { from: SOURCE_PAGE }));

    redirectToNotFound(notFoundService);

    expect(logger.warn).toHaveBeenCalledWith('Linked drug not found', {
      ...MISSING_DRUG,
      sourcePage: SOURCE_PAGE,
    });
    expect(setTag).toHaveBeenCalledWith(NAVIGATION_ARRIVAL_TAG, NavigationArrivalEnum.InApp);
    expect(logger.log).not.toHaveBeenCalled();
  });

  it("reports a warning with the referring page when one of the site's links opened in a new tab", () => {
    const setTag = jest.spyOn(Sentry.Scope.prototype, 'setTag');
    const notFoundService = setup(
      navigationTo('/drugs/CHEMBL0000000'),
      SITE_REFERRER,
      null,
      'navigate',
    );

    redirectToNotFound(notFoundService);

    expect(logger.warn).toHaveBeenCalledWith('Linked drug not found', {
      ...MISSING_DRUG,
      sourcePage: SOURCE_PAGE,
    });
    expect(setTag).toHaveBeenCalledWith(
      NAVIGATION_ARRIVAL_TAG,
      NavigationArrivalEnum.InternalNewTab,
    );
  });

  it('classifies by the in-flight navigation when the item is found missing before it ends', () => {
    const appFirstPage = navigationTo(SOURCE_PAGE);
    const notFoundService = setup(
      appFirstPage,
      '',
      navigationTo('/drugs/CHEMBL0000000', { from: SOURCE_PAGE }),
    );

    redirectToNotFound(notFoundService);

    expect(logger.warn).toHaveBeenCalledWith('Linked drug not found', {
      ...MISSING_DRUG,
      sourcePage: SOURCE_PAGE,
    });
  });

  it('shows the not-found page without changing the URL, after reporting', () => {
    const notFoundService = setup(navigationTo('/drugs/CHEMBL0000000', { from: SOURCE_PAGE }));

    redirectToNotFound(notFoundService);

    expect(navigateByUrl).toHaveBeenCalledWith(NOT_FOUND_ROUTE_PATH, { skipLocationChange: true });
    expect(logger.warn.mock.invocationCallOrder[0]).toBeLessThan(
      navigateByUrl.mock.invocationCallOrder[0],
    );
  });

  it.each<[string, NavigationArrival, Navigation, string, NavigationTimingType | undefined]>([
    [
      'typed URL',
      NavigationArrivalEnum.Direct,
      navigationTo('/drugs/CHEMBL0000000'),
      '',
      undefined,
    ],
    [
      'link on another site',
      NavigationArrivalEnum.External,
      navigationTo('/drugs/CHEMBL0000000'),
      'https://www.google.com/',
      undefined,
    ],
    [
      'back button inside the app',
      NavigationArrivalEnum.History,
      navigationTo('/drugs/CHEMBL0000000', { from: SOURCE_PAGE, trigger: 'popstate' }),
      '',
      undefined,
    ],
    [
      "reload of a page opened from the site's link",
      NavigationArrivalEnum.Reload,
      navigationTo('/drugs/CHEMBL0000000'),
      SITE_REFERRER,
      'reload',
    ],
    [
      "back button onto a page opened from the site's link",
      NavigationArrivalEnum.History,
      navigationTo('/drugs/CHEMBL0000000'),
      SITE_REFERRER,
      'back_forward',
    ],
  ])('leaves only a breadcrumb for a %s', (_, arrival, navigation, referrer, loadType) => {
    const notFoundService = setup(navigation, referrer, null, loadType);

    redirectToNotFound(notFoundService);

    expect(logger.warn).not.toHaveBeenCalled();
    expect(logger.log).toHaveBeenCalledWith('drug not found, redirecting', {
      ...MISSING_DRUG,
      arrival,
    });
  });
});
