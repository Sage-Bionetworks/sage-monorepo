import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { BattleGateService } from '@sagebionetworks/bixarena/services';
import { NavComponent, FooterComponent, LoginModalComponent } from '@sagebionetworks/bixarena/ui';
import { GtmComponent } from '@sagebionetworks/web-shared/angular/analytics/gtm';

@Component({
  imports: [RouterModule, NavComponent, FooterComponent, LoginModalComponent, GtmComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  readonly useGoogleTagManager = inject(ConfigService).config.analytics.googleTagManager.enabled;
  readonly gate = inject(BattleGateService);

  private readonly router = inject(Router);
  private readonly currentUrl = toSignal(
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      map((e) => e.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );
  readonly showFooter = computed(() => !this.currentUrl().startsWith('/battle'));
}
