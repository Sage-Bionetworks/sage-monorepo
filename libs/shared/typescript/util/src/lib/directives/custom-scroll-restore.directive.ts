import { ViewportScroller } from '@angular/common';
import { Directive, OnInit } from '@angular/core';
import { Router } from '@angular/router';

/**
 */
@Directive({
  selector: '[sageCustomScrollRestore]',
  standalone: true,
})
export class CustomScrollRestoreDirective implements OnInit {
  private scrollY = 0;

  constructor(
    private router: Router,
    private viewportScroller: ViewportScroller
  ) {}

  ngOnInit(): void {
    window.addEventListener('scroll', () => {
      this.scrollY = window.scrollY;
    });

    this.router.events.subscribe(() =>
      this.viewportScroller.scrollToPosition([0, this.scrollY])
    );
  }
}
