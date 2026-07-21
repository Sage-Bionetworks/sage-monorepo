import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  HomeCardComponent,
  SvgImageComponent,
  ToggleCardComponent,
  ToggleCardOption,
} from '@sagebionetworks/explorers/ui';
import { isModelOrganism, ModelOrganism, ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { SearchInputComponent } from '@sagebionetworks/model-ad/ui';

interface Stat {
  label: string;
  value: string;
}

@Component({
  selector: 'model-ad-home',
  imports: [ToggleCardComponent, HomeCardComponent, SvgImageComponent, SearchInputComponent],
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

  readonly selectedModelOrganism = signal<ModelOrganism>('mouse');

  readonly modelOrganismOptions: (ToggleCardOption & { value: ModelOrganism })[] = [
    {
      label: 'Mouse Models',
      value: 'mouse',
      imagePath: 'model-ad-assets/images/mouse-with-brain-network.svg',
      imageAltText: 'mouse model icon',
    },
    {
      label: 'Marmoset Models',
      value: 'marmoset',
      imagePath: 'model-ad-assets/images/marmoset-model.svg',
      imageAltText: 'marmoset model icon',
    },
  ];

  ROUTE_PATHS = ROUTE_PATHS;

  setModelOrganism(value: string | undefined) {
    if (isModelOrganism(value)) {
      this.selectedModelOrganism.set(value);
    }
  }

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
    this.breakpointObserver
      .observe([`(width < ${this.MOBILE_BREAKPOINT}px)`])
      .pipe(takeUntilDestroyed())
      .subscribe((result) => {
        this.backgroundImage.set(
          result.matches ? this.backgroundImageMobile : this.backgroundImageDesktop,
        );
      });
  }
}
