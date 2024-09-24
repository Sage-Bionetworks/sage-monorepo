import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { WikiComponent } from 'libs/agora/wiki/src/lib/wiki.component';

@Component({
  selector: 'agora-news',
  standalone: true,
  imports: [CommonModule, WikiComponent],
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss'],
})
export class NewsComponent {
  wikiId = '611426';
  className = 'news-page-content';
}
