import { ViewportScroller } from '@angular/common';
import { AfterViewInit, Directive, OnInit } from '@angular/core';
import { Event, NavigationStart, Router, Scroll } from '@angular/router';
import { filter, tap } from 'rxjs';

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
  private samePageNavigation = false;

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
    this.router.events
      .pipe(
        tap((e) => {
          if (e instanceof NavigationStart) {
            this.samePageNavigation = e.navigationTrigger === 'imperative';
          }
        }),
        filter((e: Event): e is Scroll => e instanceof Scroll)
      )
      .subscribe((e) => {
        if (e.position) {
          // backward navigation
          this.viewportScroller.scrollToPosition(e.position);
        } else if (e.anchor) {
          // anchor navigation
          this.viewportScroller.scrollToAnchor(e.anchor);
        } else if (this.samePageNavigation) {
          // same page navigation
          this.viewportScroller.scrollToPosition([this.scrollX, this.scrollY]);
        } else {
          // forward navigation
          this.viewportScroller.scrollToPosition([0, 0]);
        }
      });
  }
}
