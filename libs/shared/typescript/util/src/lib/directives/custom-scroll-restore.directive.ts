import { ViewportScroller } from '@angular/common';
import { AfterViewInit, Directive, OnInit } from '@angular/core';
import { Event, Router, RoutesRecognized, Scroll } from '@angular/router';
import { combineLatest, filter, pairwise } from 'rxjs';

/**
 * A directive for restoring scroll positions based on the method of navigation.
 * It helps preserve the user's scroll position during various types of navigation actions.
 * - Backward navigation: Scrolls to the previous position.
 * - Anchor navigation: Scrolls to a specified anchor.
 * - Param navigation: Maintains the current scroll position,
 * - Forward navigation/Same url navigation: Scrolls to the top of the page.
 */

@Directive({
  selector: '[sageCustomScrollRestore]',
  standalone: true,
})
export class CustomScrollRestoreDirective implements OnInit, AfterViewInit {
  private scrollX = 0;
  private scrollY = 0;
  private previousUrl!: string;

  constructor(
    private router: Router,
    private viewportScroller: ViewportScroller
  ) {}

  ngOnInit(): void {
    // Capture the current scroll position
    window.addEventListener('scroll', () => {
      this.scrollX = window.scrollX;
      this.scrollY = window.scrollY;
    });
  }

  ngAfterViewInit(): void {
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
        console.log(1);
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
          this.viewportScroller.scrollToPosition([this.scrollX, this.scrollY]);
        } else {
          // forward navigation / same url navigation - restore scroll position to top
          this.viewportScroller.scrollToPosition([0, 0]);
        }
      }
    );
  }
}
