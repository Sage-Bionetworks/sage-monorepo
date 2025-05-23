import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Data } from '@angular/router';
import { map } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'explorers-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent implements OnInit {
  readonly backgroundImagePath = 'explorers-assets/images/background.svg';
  email = '';

  private activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  ngOnInit() {
    this.setEmail();
  }

  private setEmail() {
    this.activatedRoute.data
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        map((data: Data) => data['supportEmail']),
      )
      .subscribe({
        next: (email) => {
          if (!email) {
            console.error('Support email not found in route data');
          } else {
            this.email = email;
          }
        },
        error: (error) => {
          console.error('Error retrieving support email:', error);
        },
      });
  }
}
