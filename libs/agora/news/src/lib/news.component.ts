
import { Component, ViewEncapsulation } from '@angular/core';
import { WikiComponent } from '@sagebionetworks/agora/shared';

@Component({
  selector: 'agora-news',
  imports: [WikiComponent],
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class NewsComponent {
  wikiId = '611426';
  className = 'news-page-content';
}
