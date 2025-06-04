import { Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeroComponent } from '@sagebionetworks/explorers/ui';
import { WikiComponent } from '@sagebionetworks/explorers/util';
import { ActivatedRoute, Data } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

@Component({
  selector: 'explorers-wiki-driven',
  imports: [CommonModule, HeroComponent, WikiComponent],
  templateUrl: './wiki-driven.component.html',
  styleUrls: ['./wiki-driven.component.scss'],
})
export class WikiDrivenComponent implements OnInit {
  private activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  @Input() title = '';
  @Input() wikiId = '';
  @Input() className = 'wiki-driven-page-content';

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
            console.error('Wiki ID not found in route data for wiki-driven component');
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
