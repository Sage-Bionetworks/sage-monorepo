import { Component, inject, input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'explorers-error-page',
  imports: [ButtonModule],
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss'],
})
export class ErrorPageComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  supportEmail = input('supportEmail'); // from the route data
  readonly backgroundImagePath = 'explorers-assets/images/background.svg';

  errorMessage = '';
  retryUrl = '';

  constructor() {
    this.route.queryParams.subscribe((params) => {
      this.errorMessage = params['message'] || 'Unknown Error';
      this.retryUrl = params['retryUrl'] || '';
    });
  }

  retry() {
    this.router.navigateByUrl(this.retryUrl);
  }

  goHome() {
    this.router.navigate(['/']);
  }
}
