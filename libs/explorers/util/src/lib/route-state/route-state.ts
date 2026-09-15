import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import { distinctUntilChanged, map, merge, Observable } from 'rxjs';

/**
 * Params and query params come in on two separate streams, and the router pushes one right after
 * the other, so combining them can give you the new query param with the old path param. The
 * snapshot is already updated before either push, so read from that and use the streams only as
 * a trigger. Both pushes hand back the same snapshot object, so distinctUntilChanged drops the
 * second one and we get a single emission per navigation.
 */
export function routeSnapshotChanges(route: ActivatedRoute): Observable<ActivatedRouteSnapshot> {
  return merge(route.paramMap, route.queryParamMap).pipe(
    map(() => route.snapshot),
    distinctUntilChanged(),
  );
}
