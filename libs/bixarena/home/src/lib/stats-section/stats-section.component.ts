import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  effect,
  inject,
  signal,
} from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { catchError, of, tap } from 'rxjs';
import { StatsService, UserService, UserStats } from '@sagebionetworks/bixarena/api-client';
import { AuthService } from '@sagebionetworks/bixarena/services';

@Component({
  selector: 'bixarena-stats-section',
  imports: [DecimalPipe],
  templateUrl: './stats-section.component.html',
  styleUrl: './stats-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatsSectionComponent {
  private readonly auth = inject(AuthService);
  private readonly userStatsService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);

  readonly stats = toSignal(
    inject(StatsService)
      .getPublicStats()
      .pipe(
        tap((s) => console.debug('✅ Fetched public stats', s)),
        catchError((err) => {
          console.error('❌ Failed to fetch public stats', err);
          return of(null);
        }),
      ),
    { initialValue: null },
  );

  // Authed-only signal: filled when the user is logged in, cleared on logout.
  // Refetched whenever isAuthenticated flips true (covers mid-session login
  // after an OIDC redirect, which technically reloads the page anyway).
  private readonly userStatsSignal = signal<UserStats | null>(null);
  readonly userStats = computed(() =>
    this.auth.isAuthenticated() ? this.userStatsSignal() : null,
  );

  constructor() {
    effect(() => {
      if (!this.auth.isAuthenticated()) {
        this.userStatsSignal.set(null);
        return;
      }
      this.userStatsService
        .getUserStats()
        .pipe(
          tap((s) => console.debug('✅ Fetched user stats', s)),
          catchError((err) => {
            console.error('❌ Failed to fetch user stats', err);
            return of(null);
          }),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe((s) => {
          // Guard against logout-while-in-flight: if auth flipped false
          // between the request and the response, drop the result.
          if (this.auth.isAuthenticated()) this.userStatsSignal.set(s);
        });
    });
  }
}
