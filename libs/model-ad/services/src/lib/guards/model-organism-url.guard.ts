import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { LoggerService } from '@sagebionetworks/explorers/services';
import { QUERY_PARAMS } from '@sagebionetworks/model-ad/config';
import { isUnknownModelOrganism, resolveModelOrganism } from '@sagebionetworks/model-ad/util';

export const UNKNOWN_MODEL_ORGANISM_MESSAGE =
  'unknown modelOrganism query param; falling back to the default';

// Ensures every model-details URL carries a valid modelOrganism query param.
// Wrong-case values are lowercased and missing or unknown values default to mouse, both via a
// redirect, so legacy URLs land on the canonical form and the param survives reloads and sharing.
// Only an unknown value is reported: missing and wrong-case values are expected in legacy URLs.
export const modelOrganismUrlGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const logger = inject(LoggerService).forSource('modelOrganismUrlGuard');
  const rawModelOrganism = route.queryParams[QUERY_PARAMS.MODEL_ORGANISM];
  const modelOrganism = resolveModelOrganism(rawModelOrganism);

  if (rawModelOrganism === modelOrganism) {
    return true;
  }

  if (isUnknownModelOrganism(rawModelOrganism)) {
    logger.warn(UNKNOWN_MODEL_ORGANISM_MESSAGE, {
      rawModelOrganism,
      fallback: modelOrganism,
      url: state.url,
    });
  }

  const urlTree = router.parseUrl(state.url);
  urlTree.queryParams = { ...urlTree.queryParams, [QUERY_PARAMS.MODEL_ORGANISM]: modelOrganism };
  return urlTree;
};
