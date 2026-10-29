import { DOCUMENT, inject, Injectable } from '@angular/core';
import { Navigation, Router } from '@angular/router';
import { NOT_FOUND_ROUTE_PATH } from '@sagebionetworks/explorers/constants';
import * as Sentry from '@sentry/angular';
import { SourceLogger } from './logger.service';

/** How the user arrived at a page. */
export const NavigationArrivalEnum = {
  /** Followed a link, or was sent there by code, inside the running app. */
  InApp: 'in-app',
  /** Opened one of the site's own links in a new tab or window. */
  InternalNewTab: 'internal-new-tab',
  /** Used the browser's back or forward button. */
  History: 'history',
  /** Reloaded the page. */
  Reload: 'reload',
  /** Followed a link on another site. */
  External: 'external',
  /** Typed, pasted or bookmarked the URL, or the referrer was withheld. */
  Direct: 'direct',
} as const;
export type NavigationArrival = (typeof NavigationArrivalEnum)[keyof typeof NavigationArrivalEnum];

export const NAVIGATION_ARRIVAL_TAG = 'navigation.arrival';

/** Arrivals through one of the site's own links, where a missing item is a data-quality issue. */
const SITE_LINK_ARRIVALS: readonly NavigationArrival[] = [
  NavigationArrivalEnum.InApp,
  NavigationArrivalEnum.InternalNewTab,
];

/**
 * Classifies how the user arrived at the current page. `navigation` is the navigation that opened
 * the page; it has a previous navigation only after the app's first page. The referrer and
 * `documentLoadType`, how the browser loaded the document, only matter on that first page. A
 * reload or a back/forward load keeps the referrer of the visit it repeats, so the load type is
 * checked first; the referrer then tells the site's own links apart from other sites' links and
 * from typed URLs.
 */
export function getNavigationArrival(
  navigation: Navigation | null,
  referrer: string,
  origin: string,
  documentLoadType?: NavigationTimingType,
): NavigationArrival {
  if (navigation?.previousNavigation) {
    return navigation.trigger === 'popstate'
      ? NavigationArrivalEnum.History
      : NavigationArrivalEnum.InApp;
  }
  if (documentLoadType === 'reload') return NavigationArrivalEnum.Reload;
  if (documentLoadType === 'back_forward') return NavigationArrivalEnum.History;
  if (!referrer) return NavigationArrivalEnum.Direct;
  try {
    return new URL(referrer).origin === origin
      ? NavigationArrivalEnum.InternalNewTab
      : NavigationArrivalEnum.External;
  } catch {
    return NavigationArrivalEnum.External;
  }
}

/**
 * Shows the not-found page for a detail page whose item is missing, and reports it. Every link on
 * the site should lead to an item that exists, so a missing item reached through one of the site's
 * own links is a data-quality issue and is reported to Sentry as a warning, with the page the link
 * was on. A missing item reached any other way, such as a typed URL or another site's link, is
 * only a breadcrumb.
 */
@Injectable({ providedIn: 'root' })
export class NotFoundService {
  private readonly router = inject(Router);
  private readonly document = inject(DOCUMENT);

  /**
   * Reports the missing item, then shows the not-found page without changing the URL. `itemType`
   * names what was missing, e.g. `drug`, and must stay fixed, since the warning's message is built
   * from it and groups the Sentry issue. `data` identifies the missing item.
   */
  redirectToNotFound(logger: SourceLogger, itemType: string, data: Record<string, unknown>): void {
    // The redirect replaces the navigation the report classifies, so the report must come first.
    this.report(logger, itemType, data);
    this.router.navigateByUrl(NOT_FOUND_ROUTE_PATH, { skipLocationChange: true });
  }

  private report(logger: SourceLogger, itemType: string, data: Record<string, unknown>): void {
    // A detail page can learn its item is missing during the navigation that opened it, e.g. from a
    // cached response, or after it, from a slower request; either way this is that navigation.
    const navigation = this.router.currentNavigation() ?? this.router.lastSuccessfulNavigation;
    const arrival = getNavigationArrival(
      navigation,
      this.document.referrer,
      this.document.location.origin,
      this.documentLoadType(),
    );

    if (!SITE_LINK_ARRIVALS.includes(arrival)) {
      logger.log(`${itemType} not found, redirecting`, { ...data, arrival });
      return;
    }

    const sourcePage =
      arrival === NavigationArrivalEnum.InApp ? this.previousUrl(navigation) : this.referrerPath();
    Sentry.withScope((scope) => {
      scope.setTag(NAVIGATION_ARRIVAL_TAG, arrival);
      logger.warn(`Linked ${itemType} not found`, { ...data, sourcePage });
    });
  }

  private documentLoadType(): NavigationTimingType | undefined {
    const [entry] = this.document.defaultView?.performance?.getEntriesByType('navigation') ?? [];
    return (entry as PerformanceNavigationTiming | undefined)?.type;
  }

  private previousUrl(navigation: Navigation | null): string | undefined {
    const previous = navigation?.previousNavigation;
    const url = previous?.finalUrl ?? previous?.extractedUrl;
    return url ? this.router.serializeUrl(url) : undefined;
  }

  private referrerPath(): string {
    const referrer = new URL(this.document.referrer);
    return `${referrer.pathname}${referrer.search}`;
  }
}
