import { Component } from '@angular/core';
import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH } from '@sagebionetworks/agora/config';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { WikiComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'agora-about',
  imports: [WikiComponent],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  wikiParams: SynapseWikiParams = { ownerId: 'syn25913473', wikiId: '612058' };

  readonly heroBackgroundImagePath = DEFAULT_HERO_BACKGROUND_IMAGE_PATH;
}
