import { ViewportScroller } from '@angular/common';
import { AfterViewInit, Directive, OnInit } from '@angular/core';
import { Event, Router, Scroll } from '@angular/router';
import { filter } from 'rxjs';

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
  private scrollY = 0;
  private currentBaseUrl!: string;

  constructor(
    private router: Router,
    private viewportScroller: ViewportScroller
  ) {
    window.addEventListener('scroll', () => {
      this.scrollY = window.scrollY;
    });
  }

  ngOnInit(): void {
    // Capture the initial scroll position and the current base URL
    window.addEventListener('scroll', () => {
      this.scrollY = window.scrollY;
    });

    this.currentBaseUrl = this.router.url.split('?')[0];
  }

  ngAfterViewInit(): void {
    // Listen to router events to determine the appropriate scroll restoration method.
    this.router.events
      .pipe(filter((e: Event): e is Scroll => e instanceof Scroll))
      .subscribe((e) => {
        if (e.position) {
          // backward navigation
          this.viewportScroller.scrollToPosition(e.position);
        } else if (e.anchor) {
          // anchor navigation
          this.viewportScroller.scrollToAnchor(e.anchor);
        } else if (e.routerEvent.url.split('?')[0] === this.currentBaseUrl) {
          // same page navigation
          this.viewportScroller.scrollToPosition([0, this.scrollY]);
        } else {
          // forward navigation
          this.viewportScroller.scrollToPosition([0, 0]);
        }
      });
  }
}
