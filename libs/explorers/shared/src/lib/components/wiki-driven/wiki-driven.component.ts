import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeroComponent } from '@sagebionetworks/explorers/ui';
import { WikiComponent } from '@sagebionetworks/explorers/util';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { getRouteData } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-wiki-driven',
  imports: [CommonModule, HeroComponent, WikiComponent],
  templateUrl: './wiki-driven.component.html',
  styleUrls: ['./wiki-driven.component.scss'],
})
export class WikiDrivenComponent implements OnInit {
  private activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  heroTitle = '';
  wikiId = '';
  className = 'wiki-driven-page-content';

  ngOnInit() {
    this.getRouteParams();
  }

  getRouteParams() {
    this.activatedRoute.data.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.wikiId = getRouteData('wikiId', data);
        this.heroTitle = getRouteData('heroTitle', data);
      },
      error: (error) => {
        console.error('Error retrieving route params:', error);
      },
    });
  }
}
