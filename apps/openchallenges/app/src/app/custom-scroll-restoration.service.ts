import { ViewportScroller } from '@angular/common';
import { Injectable } from '@angular/core';
import { Event, Router, RoutesRecognized, Scroll } from '@angular/router';
import { combineLatest, filter, pairwise } from 'rxjs';

/**
 * A service for restoring scroll positions based on the method of navigation.
 * It helps preserve the user's scroll position during various types of navigation actions.
 * - Backward navigation: Scrolls to the previous position.
 * - Anchor navigation: Scrolls to a specified anchor.
 * - Param navigation: Maintains the current scroll position,
 * - Forward navigation/Same url navigation: Scrolls to the top of the page.
 */

@Injectable({
  providedIn: 'root',
})
export class CustomScrollRestorationService {
  private currentScrollPos: [number, number] = [0, 0];

  constructor(
    private router: Router,
    private viewportScroller: ViewportScroller
  ) {}

  scrollPositionRestoration(): void {
    // Capture the current scroll position
    this.currentScrollPos = this.viewportScroller.getScrollPosition();

    const routesRecognized$ = this.router.events.pipe(
      filter(
        (e: Event): e is RoutesRecognized => e instanceof RoutesRecognized
      ),
      pairwise()
    );

    const scroll$ = this.router.events.pipe(
      filter((e: Event): e is Scroll => e instanceof Scroll)
    );

    // listen to router events to determine the appropriate scroll restoration method
    combineLatest([routesRecognized$, scroll$]).subscribe(
      ([routeEvents, scrollEvent]) => {
        const previousUrl = routeEvents[0].urlAfterRedirects;
        const currentUrl = routeEvents[1].urlAfterRedirects;
        const isSamePageUrl = previousUrl === currentUrl;

        if (scrollEvent.position) {
          // backward navigation
          this.viewportScroller.scrollToPosition(scrollEvent.position);
        } else if (scrollEvent.anchor) {
          // anchor navigation
          this.viewportScroller.scrollToAnchor(scrollEvent.anchor);
        } else if (
          !isSamePageUrl &&
          previousUrl.split('?')[0] === currentUrl.split('?')[0]
        ) {
          // param navigation excluding exact the same full url
          this.viewportScroller.scrollToPosition(this.currentScrollPos);
        } else {
          // forward navigation / same url navigation - restore scroll position to top
          this.viewportScroller.scrollToPosition([0, 0]);
        }
      }
    );
  }
}
