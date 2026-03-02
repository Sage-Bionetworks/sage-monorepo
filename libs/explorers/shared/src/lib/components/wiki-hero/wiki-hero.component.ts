import { Component, computed, input } from '@angular/core';

import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { HeroComponent } from '@sagebionetworks/explorers/ui';
import { WikiComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-wiki-hero',
  imports: [HeroComponent, WikiComponent],
  templateUrl: './wiki-hero.component.html',
  styleUrls: ['./wiki-hero.component.scss'],
})
export class WikiHeroComponent {
  wikiParams = input.required<SynapseWikiParams>(); // from the route data
  heroTitle = input('heroTitle'); // from the route data
  heroBackgroundImagePath = input<string | undefined>(); // from the route data
  className = input<string>(''); // from the route data

  heroBackgroundImagePathOrDefault = computed(
    () => this.heroBackgroundImagePath() || 'explorers-assets/images/background.svg',
  );
}
