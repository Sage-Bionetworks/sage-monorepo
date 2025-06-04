import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WikiDrivenComponent } from '../wiki-driven/wiki-driven.component';
import { ActivatedRoute, Data } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

@Component({
  selector: 'explorers-news',
  imports: [CommonModule, WikiDrivenComponent],
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss'],
})
export class NewsComponent implements OnInit {
  private activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  wikiId = '';
  className = 'news-page-content';

  ngOnInit() {
    this.setWikiId();
  }

  setWikiId() {
    this.activatedRoute.data
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        map((data: Data) => data['wikiId'] as string),
      )
      .subscribe({
        next: (wikiId) => {
          if (!wikiId) {
            console.error('Wiki ID not found in route data for news component');
          } else {
            this.wikiId = wikiId;
          }
        },
        error: (error) => {
          console.error('Error retrieving wiki ID:', error);
        },
      });
  }
}
