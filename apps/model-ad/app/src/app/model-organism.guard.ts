import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { resolveModelOrganism } from '@sagebionetworks/model-ad/util';

// Ensures every model-details URL carries a valid modelOrganism query param.
// Missing, invalid, or wrong-case values are normalized (defaulting to mouse) via a redirect,
// so legacy URLs land on the canonical form and the param survives reloads and sharing.
export const modelOrganismGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const rawModelOrganism = route.queryParams['modelOrganism'];
  const modelOrganism = resolveModelOrganism(rawModelOrganism);

  if (rawModelOrganism === modelOrganism) {
    return true;
  }

  const urlTree = router.parseUrl(state.url);
  urlTree.queryParams = { ...urlTree.queryParams, modelOrganism };
  return urlTree;
};
