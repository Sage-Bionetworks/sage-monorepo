import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, computed, inject, signal } from '@angular/core';
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

interface OrganismContent {
  methodologyDescription: string;
  stats: Stat[];
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

  // Split so the lower arc can be anchored to the stats, whose offset shifts with the selected
  // model organism.
  private readonly upperArcImageDesktop = 'model-ad-assets/images/home-arc-bg-upper.svg';
  private readonly upperArcImageMobile = 'model-ad-assets/images/home-arc-bg-upper-mobile.svg';
  private readonly lowerArcImageDesktop = 'model-ad-assets/images/home-arc-bg-lower.svg';
  private readonly lowerArcImageMobile = 'model-ad-assets/images/home-arc-bg-lower-mobile.svg';

  private readonly isMobile = signal(false);

  readonly upperArcImage = computed(() =>
    this.isMobile() ? this.upperArcImageMobile : this.upperArcImageDesktop,
  );
  readonly lowerArcImage = computed(() =>
    this.isMobile() ? this.lowerArcImageMobile : this.lowerArcImageDesktop,
  );

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

  private readonly organismContentByModelOrganism: Record<ModelOrganism, OrganismContent> = {
    mouse: {
      methodologyDescription:
        "MODEL-AD comprises two research centers with complementary approaches to generating new mouse models that more faithfully recapitulate features of Alzheimer's disease in humans. Mouse models are phenotyped using standardized neuropathology, 'omics, and behavioral measures.",
      stats: [
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
      ],
    },
    marmoset: {
      methodologyDescription:
        "MARMO-AD is establishing marmoset models of Alzheimer's disease to identify emerging phenotypes and illuminate mechanisms underlying pathogenesis. Marmoset models are assessed for genetic, molecular, functional, behavioral, and pathological phenotypes to reveal the earliest cellular and molecular events of primate-specific aging and dementia.",
      stats: [
        {
          label: 'Institutions',
          value: '5+',
        },
        {
          label: 'Genes',
          value: '30K+',
        },
        {
          label: 'Models',
          value: '1+',
        },
      ],
    },
  };

  readonly organismContent = computed(
    () => this.organismContentByModelOrganism[this.selectedModelOrganism()],
  );

  constructor() {
    this.breakpointObserver
      .observe([`(width < ${this.MOBILE_BREAKPOINT}px)`])
      .pipe(takeUntilDestroyed())
      .subscribe((result) => this.isMobile.set(result.matches));
  }
}
