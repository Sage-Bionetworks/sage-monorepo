import { Component, ViewEncapsulation } from '@angular/core';
import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH } from '@sagebionetworks/agora/config';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { WikiComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'agora-news',
  imports: [WikiComponent],
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class NewsComponent {
  wikiParams: SynapseWikiParams = { ownerId: 'syn25913473', wikiId: '611426' };

  readonly heroBackgroundImagePath = DEFAULT_HERO_BACKGROUND_IMAGE_PATH;
}
