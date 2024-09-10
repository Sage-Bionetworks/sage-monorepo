import { from, Observable } from 'rxjs';
import { last, map, mergeMap, toArray } from 'rxjs/operators';

// Source: https://stackoverflow.com/a/54247150
export function forkJoinConcurrent(
  observables: Observable<any>[],
  concurrent: number,
): Observable<any[]> {
  // Convert the array of observables to a higher-order observable:
  return from(observables).pipe(
    // Merge each of the observables in the higher-order observable
    // into a single stream:
    mergeMap(
      (observable, observableIndex) =>
        observable.pipe(
          // Like forkJoin, we're interested only in the last value:
          last(),
          // Combine the value with the index so that the stream of merged
          // values - which could be in any order - can be sorted to match
          // the order of the source observables:
          map((value) => ({ index: observableIndex, value })),
        ),
      concurrent,
    ),
    // Convert the stream of last values to an array:
    toArray(),
    // Sort the array of value/index pairs by index - so the value
    // indices correspond to the source observable indices and then
    // map the pair to the value:
    map((pairs) => pairs.sort((l, r) => l.index - r.index).map((pair) => pair.value)),
  );
}
