import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WikiDrivenComponent } from '../wiki-driven/wiki-driven.component';
import { ActivatedRoute, Data } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

@Component({
  selector: 'explorers-about',
  imports: [CommonModule, WikiDrivenComponent],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent implements OnInit {
  private activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  wikiId = '';
  className = 'about-page-content';

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
            console.error('Wiki ID not found in route data for about component');
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
