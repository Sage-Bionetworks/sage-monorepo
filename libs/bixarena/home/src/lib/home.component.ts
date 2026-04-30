import { ChangeDetectionStrategy, Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService, BattleGateService } from '@sagebionetworks/bixarena/services';
import { ComposerSectionComponent } from './composer-section/composer-section.component';
import { LeaderboardSectionComponent } from './leaderboard-section/leaderboard-section.component';
import { StatsSectionComponent } from './stats-section/stats-section.component';
import { TrendingSectionComponent } from './trending-section/trending-section.component';

@Component({
  selector: 'bixarena-home',
  imports: [
    ComposerSectionComponent,
    StatsSectionComponent,
    TrendingSectionComponent,
    LeaderboardSectionComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly gate = inject(BattleGateService);
  private readonly router = inject(Router);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  // OIDC always lands users back on /. Resume the round-trip when a pending
  // prompt is waiting; drop stale pending if they came back unauthenticated
  // (failed/abandoned login) so the next sign-in won't surprise-redirect.
  ngOnInit(): void {
    if (!this.isBrowser || !this.gate.hasPendingPrompt()) return;
    if (this.auth.isAuthenticated()) {
      void this.router.navigate(['/battle']);
    } else {
      this.gate.clearPending();
    }
  }
}
