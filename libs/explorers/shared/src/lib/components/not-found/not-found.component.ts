import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { getRouteData } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent implements OnInit {
  private activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  readonly backgroundImagePath = 'explorers-assets/images/background.svg';
  email = '';

  ngOnInit() {
    this.setEmail();
  }

  private setEmail() {
    this.activatedRoute.data.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.email = getRouteData('supportEmail', data);
      },
      error: (error) => {
        console.error('Error retrieving support email:', error);
      },
    });
  }
}
