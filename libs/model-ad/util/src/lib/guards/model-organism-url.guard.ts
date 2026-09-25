import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { MODEL_ORGANISM_QUERY_KEY, resolveModelOrganism } from '../model-organism';

// Ensures every model-details URL carries a valid modelOrganism query param.
// Wrong-case values are lowercased and missing or unknown values default to mouse, both via a
// redirect, so legacy URLs land on the canonical form and the param survives reloads and sharing.
export const modelOrganismUrlGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const rawModelOrganism = route.queryParams[MODEL_ORGANISM_QUERY_KEY];
  const modelOrganism = resolveModelOrganism(rawModelOrganism);

  if (rawModelOrganism === modelOrganism) {
    return true;
  }

  const urlTree = router.parseUrl(state.url);
  urlTree.queryParams = { ...urlTree.queryParams, [MODEL_ORGANISM_QUERY_KEY]: modelOrganism };
  return urlTree;
};
