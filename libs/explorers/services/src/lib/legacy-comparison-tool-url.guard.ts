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

// Builds a guard that rewrites a legacy comparison tool share URL into its current shape before the
// tool loads. The resolveRedirect function supplies the product-specific translation rules; this
// factory owns the redirect semantics and shares its encoding with ComparisonToolUrlService.
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

      const urlTree = router.parseUrl(state.url);
      urlTree.queryParams = applyParams(
        urlTree.queryParams,
        serializeComparisonToolUrlParams(redirect, route.queryParams),
      );

      // Replace rather than push, so the back button returns to wherever the legacy link was opened
      // from instead of the legacy URL this guard just redirected away from.
      return new RedirectCommand(urlTree, { replaceUrl: true });
    } catch (error) {
      // Don't rethrow: a guard that throws cancels the navigation and breaks the page. Returning true
      // loads the original URL instead, and the comparison tool falls back to its defaults for
      // anything it doesn't recognize.
      //
      // LoggerService.error sends only the error object to Sentry, not the message, so the URL goes
      // in a new Error's message and the original error is kept as its cause.
      logger.error(
        LEGACY_URL_TRANSLATION_FAILED_MESSAGE,
        new Error(`${LEGACY_URL_TRANSLATION_FAILED_MESSAGE}: ${state.url}`, { cause: error }),
      );
      return true;
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
