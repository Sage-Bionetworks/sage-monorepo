import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  effect,
  inject,
  OnInit,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { DecimalPipe, isPlatformBrowser } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import {
  PublicStats,
  StatsService,
  UserService,
  UserStats,
} from '@sagebionetworks/bixarena/api-client';
import { AuthService } from '@sagebionetworks/bixarena/services';

@Component({
  selector: 'bixarena-stats-section',
  imports: [DecimalPipe],
  templateUrl: './stats-section.component.html',
  styleUrl: './stats-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatsSectionComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly statsService = inject(StatsService);
  private readonly userStatsService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly stats = signal<PublicStats | null>(null);

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
      if (!this.isBrowser) return;
      this.userStatsService
        .getUserStats()
        .pipe(
          catchError(() => of(null)),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe((s) => {
          // Drop if logout raced the in-flight request.
          if (this.auth.isAuthenticated()) this.userStatsSignal.set(s);
        });
    });
  }

  ngOnInit(): void {
    if (!this.isBrowser) return;
    this.statsService
      .getPublicStats()
      .pipe(
        catchError(() => of(null)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((s) => this.stats.set(s));
  }
}
