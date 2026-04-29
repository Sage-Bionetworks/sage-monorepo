import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  OnInit,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import {
  BiomedicalCategory,
  ExamplePrompt,
  ExamplePromptService,
  ExamplePromptSort,
} from '@sagebionetworks/bixarena/api-client';
import { AuthService, BattleGateService } from '@sagebionetworks/bixarena/services';
import { PromptCardComponent } from '@sagebionetworks/bixarena/ui';

const PAGE_SIZE = 3;
const LOOKBACK_DAYS = 7;

function formatCategory(slug: BiomedicalCategory): string {
  return slug
    .split('-')
    .map((w, i) => (i > 0 && w === 'and' ? w : w.charAt(0).toUpperCase() + w.slice(1)))
    .join(' ');
}

@Component({
  selector: 'bixarena-trending-section',
  imports: [PromptCardComponent],
  templateUrl: './trending-section.component.html',
  styleUrl: './trending-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrendingSectionComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly gate = inject(BattleGateService);
  private readonly router = inject(Router);
  private readonly examplePrompts = inject(ExamplePromptService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly skeletonSlots = Array.from({ length: PAGE_SIZE }, (_, i) => i);

  private readonly prompts = signal<ExamplePrompt[]>([]);
  private readonly loading = signal(true);
  private readonly hidden = signal(false);

  readonly visible = computed(() => !this.hidden());
  readonly showSkeletons = computed(() => this.loading());
  readonly cards = this.prompts.asReadonly();

  ngOnInit(): void {
    if (!this.isBrowser) {
      this.loading.set(false);
      this.hidden.set(true);
      return;
    }

    this.examplePrompts
      .listExamplePrompts({
        sort: ExamplePromptSort.Usage,
        lookback: LOOKBACK_DAYS,
        pageSize: PAGE_SIZE,
      })
      .pipe(
        catchError(() => of(null)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((page) => {
        this.loading.set(false);
        const items = page?.examplePrompts ?? [];
        if (items.length === 0) {
          this.hidden.set(true);
          return;
        }
        this.prompts.set(items);
      });
  }

  onCardClick(p: ExamplePrompt): void {
    this.gate.savePendingPrompt(p.question, p.id);
    if (this.auth.isAuthenticated()) {
      void this.router.navigate(['/battle']);
    } else {
      this.gate.showLoginModal.set(true);
    }
  }

  categoryLabel(p: ExamplePrompt): string | undefined {
    return p.category ? formatCategory(p.category) : undefined;
  }
}
