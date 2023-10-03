// import { ViewportScroller } from '@angular/common';
// import { DestroyRef, Injectable, inject } from '@angular/core';
// import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
// import { Router, Scroll } from '@angular/router';
// import { BehaviorSubject } from 'rxjs';
// import { filter, map, switchMap } from 'rxjs/operators';

// @Injectable({
//   providedIn: 'root',
// })
// export class AutoScrollService {
//   private readonly router = inject(Router);
//   private readonly viewportScroller = inject(ViewportScroller);
//   private destroyRef = inject(DestroyRef);

//   shouldScroll = new BehaviorSubject<boolean>(false);
//   private readonly shouldScroll$ = this.shouldScroll.asObservable();

//   constructor() {
//     this.init();
//   }

//   init() {
//     const position$ = this.router.events.pipe(
//       filter((event: any) => event instanceof Scroll),
//       map((event: Scroll) => event.position)
//     );

//     position$
//       .pipe(
//         switchMap((position) =>
//           this.shouldScroll$.pipe(
//             filter(Boolean),
//             map(() => position)
//           )
//         ),
//         takeUntilDestroyed(this.destroyRef)
//       )
//       .subscribe({
//         next: (position) => {
//           this.viewportScroller.scrollToPosition(position || [0, 0]);
//         },
//       });
//   }

//   setScroll(): void {
//     this.shouldScroll.next(true);
//   }
// }

import { Injectable } from '@angular/core';
import { ViewportScroller } from '@angular/common';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class CustomScrollRestoration {
  private scrollY = 0;

  constructor(
    private router: Router,
    private viewportScroller: ViewportScroller
  ) {
    window.addEventListener('scroll', () => {
      this.scrollY = window.scrollY;
    });
  }

  previousPosition(): void {
    this.router.events.subscribe(() =>
      this.viewportScroller.scrollToPosition([0, this.scrollY])
    );
  }
}
