import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HomeCardComponent, SvgImageComponent } from '@sagebionetworks/explorers/ui';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { SearchInputComponent } from '@sagebionetworks/model-ad/ui';

interface Stat {
  label: string;
  value: string;
}

@Component({
  selector: 'model-ad-home',
  imports: [HomeCardComponent, SvgImageComponent, SearchInputComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  private readonly breakpointObserver = inject(BreakpointObserver);

  // Must match $home-mobile-md-max-width in home.component.scss
  private readonly MOBILE_BREAKPOINT = 850;

  readonly backgroundImageDesktop = 'model-ad-assets/images/home-arc-bg.svg';
  readonly backgroundImageMobile = 'model-ad-assets/images/home-arc-bg-mobile.svg';

  readonly backgroundImage = signal(this.backgroundImageDesktop);

  ROUTE_PATHS = ROUTE_PATHS;

  stats: Stat[] = [
    {
      label: 'Institutions',
      value: '5+',
    },
    {
      label: 'Genes',
      value: '20K+',
    },
    {
      label: 'Models',
      value: '15+',
    },
  ];

  constructor() {
    // Subtract 1px to match the SCSS media query logic (width < 850px = max-width: 849px)
    this.breakpointObserver
      .observe([`(max-width: ${this.MOBILE_BREAKPOINT - 1}px)`])
      .pipe(takeUntilDestroyed())
      .subscribe((result) => {
        this.backgroundImage.set(
          result.matches ? this.backgroundImageMobile : this.backgroundImageDesktop,
        );
      });
  }
}
