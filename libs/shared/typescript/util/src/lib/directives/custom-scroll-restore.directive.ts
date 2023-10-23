import { ViewportScroller } from '@angular/common';
import { AfterViewInit, Directive, OnInit } from '@angular/core';
import {
  Event,
  NavigationStart,
  Router,
  RouterEvent,
  RoutesRecognized,
  Scroll,
} from '@angular/router';
import { Observable, combineLatest, filter, pairwise, tap } from 'rxjs';

/**
 * A directive for restoring scroll positions based on the method of navigation.
 * It helps preserve the user's scroll position during various types of navigation actions.
 * - Backward navigation: Scrolls to the previous position.
 * - Anchor navigation: Scrolls to a specified anchor.
 * - Same page navigation: Maintains the current scroll position.
 * - Forward navigation: Scrolls to the top of the page.
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
    // Listen to router events to determine the appropriate scroll restoration method.
    const routesRecognized$ = this.router.events.pipe(
      filter(
        (e: Event): e is RoutesRecognized => e instanceof RoutesRecognized
      ),
      pairwise()
    );

    const scroll$ = this.router.events.pipe(
      filter((e: Event): e is Scroll => e instanceof Scroll)
    );

    combineLatest([routesRecognized$, scroll$]).subscribe(
      ([routeEvents, scrollEvent]) => {
        const previousUrl = routeEvents[0].urlAfterRedirects;
        const currentUrl = routeEvents[1].urlAfterRedirects;

        if (scrollEvent.position) {
          // backward navigation
          this.viewportScroller.scrollToPosition(scrollEvent.position);
        } else if (scrollEvent.anchor) {
          // anchor navigation
          this.viewportScroller.scrollToAnchor(scrollEvent.anchor);
        } else if (previousUrl.split('?')[0] === currentUrl.split('?')[0]) {
          // same page navigation
          this.viewportScroller.scrollToPosition([this.scrollX, this.scrollY]);
        } else {
          // forward navigation - restore scroll position to top
          this.viewportScroller.scrollToPosition([0, 0]);
        }
      }
    );

    // this.router.events
    //   .pipe(filter((e: Event): e is Scroll => e instanceof Scroll))
    //   .subscribe((e) => {
    //     if (e.position) {
    //       // backward navigation
    //       this.viewportScroller.scrollToPosition(e.position);
    //     } else if (e.anchor) {
    //       // anchor navigation
    //       this.viewportScroller.scrollToAnchor(e.anchor);
    //     } else if (
    //       this.previousUrl.split('?')[0] === e.routerEvent.url.split('?')[0]
    //     ) {
    //       // same page navigation
    //       this.viewportScroller.scrollToPosition([this.scrollX, this.scrollY]);
    //     } else {
    //       // forward navigation - restore scroll position to top
    //       this.viewportScroller.scrollToPosition([0, 0]);
    //     }
    //   });
  }
}
