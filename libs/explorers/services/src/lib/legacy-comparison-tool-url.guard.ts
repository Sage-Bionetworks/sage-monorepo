import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Params,
  RedirectCommand,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { LegacyComparisonToolUrlRedirectFn } from '@sagebionetworks/explorers/models';
import {
  deserializeComparisonToolUrlParams,
  serializeComparisonToolUrlParams,
} from './comparison-tool-url-params';
import { LoggerService } from './logger.service';

export const LEGACY_URL_TRANSLATION_FAILED_MESSAGE =
  'createLegacyComparisonToolUrlGuard: failed to translate legacy URL';
export const LEGACY_URL_REDIRECTED_MESSAGE =
  'createLegacyComparisonToolUrlGuard: redirected legacy URL';
export const LEGACY_URL_IN_APP_NAVIGATION_MESSAGE =
  'createLegacyComparisonToolUrlGuard: redirected legacy URL reached by in-app navigation';

// Builds a guard that rewrites a legacy comparison tool share URL into its current shape before the
// tool loads. The resolveRedirect function supplies the product-specific translation rules; this
// factory owns the redirect semantics and shares its encoding with ComparisonToolUrlService.
//
// Share URLs carry no version, so resolveRedirect recognizes a legacy URL by its shape. That holds
// only while the legacy shape can't occur in a current URL; a URL change whose old and new shapes
// could be confused has to introduce an explicit URL version instead.
//
// Register it with `runGuardsAndResolvers: 'paramsOrQueryParamsChange'`. The tool's URL is all query
// params, and by default a query-only navigation skips canActivate, so a legacy URL reached from
// within the tool's own page would otherwise bypass the guard.
export function createLegacyComparisonToolUrlGuard(
  resolveRedirect: LegacyComparisonToolUrlRedirectFn,
): CanActivateFn {
  return (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const router = inject(Router);
    const logger = inject(LoggerService);

    try {
      const redirect = resolveRedirect(deserializeComparisonToolUrlParams(route.queryParams));

      if (redirect === null) {
        return true;
      }

      for (const warning of redirect.warnings ?? []) {
        logger.warn(warning.message, { ...warning.data, url: state.url });
      }

      for (const note of redirect.notes ?? []) {
        logger.log(note.message, { ...note.data, url: state.url });
      }

      const urlTree = router.parseUrl(state.url);
      urlTree.queryParams = applyParams(
        urlTree.queryParams,
        serializeComparisonToolUrlParams(redirect, route.queryParams),
      );

      const redirectData = { from: state.url, to: router.serializeUrl(urlTree) };

      // Opening a legacy share link is the expected path, so it only marks the breadcrumb trail for
      // any later error on the page. A legacy URL reached after the app has already navigated came
      // from inside the session instead (a stale in-app link, or history from before the release),
      // which is worth tracking down.
      if (router.navigated) {
        logger.warn(LEGACY_URL_IN_APP_NAVIGATION_MESSAGE, redirectData);
      } else {
        logger.log(LEGACY_URL_REDIRECTED_MESSAGE, redirectData);
      }

      // Replace rather than push, so the back button returns to wherever the legacy link was opened
      // from instead of the legacy URL this guard just redirected away from.
      return new RedirectCommand(urlTree, { replaceUrl: true });
    } catch (error) {
      // Don't rethrow: a guard that throws cancels the navigation and breaks the page.
      //
      // LoggerService.error sends only the error object to Sentry, not the message, so the URL goes
      // in a new Error's message and the original error is kept as its cause.
      logger.error(
        LEGACY_URL_TRANSLATION_FAILED_MESSAGE,
        new Error(`${LEGACY_URL_TRANSLATION_FAILED_MESSAGE}: ${state.url}`, { cause: error }),
      );

      // Loading the untranslated URL would hand the tool params it can't read, such as pins in a
      // format the API rejects, so reset to the tool's default view instead. A URL that is already
      // bare has nothing left to reset, and redirecting it again would loop.
      const urlTree = router.parseUrl(state.url);
      if (Object.keys(urlTree.queryParams).length === 0) {
        return true;
      }
      urlTree.queryParams = {};
      return new RedirectCommand(urlTree, { replaceUrl: true });
    }
  };
}

// A removal has to delete the key: a UrlTree serializes null and undefined literally, unlike the
// null-means-remove convention Router.navigate applies to its queryParams.
function applyParams(queryParams: Params, patch: Params): Params {
  const merged = { ...queryParams };

  for (const [key, value] of Object.entries(patch)) {
    if (value === null) {
      delete merged[key];
    } else {
      merged[key] = value;
    }
  }

  return merged;
}
