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

// Builds a guard that rewrites a legacy comparison tool share URL into its current shape before the
// tool loads. The resolveRedirect function supplies the product-specific translation rules; this
// factory owns the redirect semantics and shares its encoding with ComparisonToolUrlService.
export function createLegacyComparisonToolUrlGuard(
  resolveRedirect: LegacyComparisonToolUrlRedirectFn,
): CanActivateFn {
  return (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const router = inject(Router);
    const logger = inject(LoggerService);

    const redirect = resolveRedirect(deserializeComparisonToolUrlParams(route.queryParams));

    if (redirect === null) {
      return true;
    }

    if (redirect.warning) {
      logger.warn(redirect.warning);
    }

    const urlTree = router.parseUrl(state.url);
    urlTree.queryParams = applyParams(
      urlTree.queryParams,
      serializeComparisonToolUrlParams(redirect, route.queryParams),
    );

    // Replace rather than push, so the back button returns to wherever the legacy link was opened
    // from instead of the legacy URL this guard just redirected away from.
    return new RedirectCommand(urlTree, { replaceUrl: true });
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
