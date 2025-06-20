
import { Component } from '@angular/core';
import { WikiComponent } from '@sagebionetworks/agora/shared';

@Component({
  selector: 'agora-about',
  imports: [WikiComponent],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  wikiId = '612058';
  className = 'about-page-content';
}
