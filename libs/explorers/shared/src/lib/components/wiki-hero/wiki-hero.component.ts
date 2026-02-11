import { Component, input } from '@angular/core';

import { HeroComponent } from '@sagebionetworks/explorers/ui';
import { WikiComponent } from '@sagebionetworks/explorers/util';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'explorers-wiki-hero',
  imports: [HeroComponent, WikiComponent],
  templateUrl: './wiki-hero.component.html',
  styleUrls: ['./wiki-hero.component.scss'],
})
export class WikiHeroComponent {
  wikiParams = input<SynapseWikiParams>(); // from the route data
  heroTitle = input('heroTitle'); // from the route data
  className = 'wiki-hero-page-content';
}
