import { ViewportScroller } from '@angular/common';
import { Directive, OnInit, DestroyRef } from '@angular/core';
import { Router, Scroll } from '@angular/router';
import { filter, map, switchMap, BehaviorSubject, tap } from 'rxjs';

import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Directive({
  selector: '[sageDisableScrollPositionRestoration]',
  standalone: true,
})
export class DisableScrollPositionRestorationDirective implements OnInit {
  shouldScroll = new BehaviorSubject<boolean>(false);
  private readonly shouldScroll$ = this.shouldScroll.asObservable();

  constructor(
    private router: Router,
    private viewportScroller: ViewportScroller,
    private destroyRef: DestroyRef
  ) {}

  ngOnInit() {
    const position$ = this.router.events.pipe(
      tap((ss) => console.log(ss)),
      filter((event: any) => event instanceof Scroll),
      map((event: Scroll) => event.position)
    );

    position$
      .pipe(
        switchMap((position) =>
          this.shouldScroll$.pipe(
            filter(Boolean),
            map(() => position)
          )
        ),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (position) => {
          this.viewportScroller.scrollToPosition(position || [0, 0]);
        },
      });
  }

  ngAfterViewInit(): void {
    this.shouldScroll.next(true);
  }
}
