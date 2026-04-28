import {
  ChangeDetectionStrategy,
  Component,
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
  ExamplePromptSearchQuery,
  ExamplePromptService,
  ExamplePromptSort,
} from '@sagebionetworks/bixarena/api-client';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService, BattleGateService } from '@sagebionetworks/bixarena/services';
import { PromptComposerComponent } from '@sagebionetworks/bixarena/ui';

const STATIC_PLACEHOLDER = 'Ask anything biomedical...';
const TYPE_INTERVAL_MS = 35;

@Component({
  selector: 'bixarena-composer-section',
  imports: [PromptComposerComponent],
  templateUrl: './composer-section.component.html',
  styleUrl: './composer-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComposerSectionComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly gate = inject(BattleGateService);
  private readonly router = inject(Router);
  private readonly examplePrompts = inject(ExamplePromptService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly promptLengthLimit = inject(ConfigService).config.battle.promptLengthLimit;
  readonly placeholder = signal(STATIC_PLACEHOLDER);

  private intervalId: ReturnType<typeof setInterval> | null = null;
  private targetPrompt: string | null = null;
  private interacted = false;

  ngOnInit(): void {
    if (!this.isBrowser) return;

    const query: ExamplePromptSearchQuery = {
      sort: ExamplePromptSort.Random,
      pageSize: 1,
      active: true,
    };
    this.examplePrompts
      .listExamplePrompts(query)
      .pipe(
        catchError(() => of(null)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((page) => {
        const prompt = page?.examplePrompts?.[0]?.question;
        if (!prompt || this.interacted) return;
        if (this.prefersReducedMotion()) {
          this.targetPrompt = prompt;
          this.placeholder.set(prompt);
        } else {
          this.beginTyping(prompt);
        }
      });

    this.destroyRef.onDestroy(() => this.clearTypingInterval());
  }

  // Bound from the section wrapper's `(focusin)` — fires on click/focus into
  // the composer's textarea (event bubbles up). One-shot: once the user has
  // engaged, animation completes and never re-runs even after blur.
  onComposerFocus(): void {
    if (this.interacted) return;
    this.interacted = true;
    this.completeTyping();
  }

  onPromptSubmit(prompt: string): void {
    this.gate.savePendingPrompt(prompt);
    if (this.auth.isAuthenticated()) {
      void this.router.navigate(['/battle']);
    } else {
      this.gate.showLoginModal.set(true);
    }
  }

  private beginTyping(text: string): void {
    this.targetPrompt = text;
    this.placeholder.set('');
    let i = 0;
    this.intervalId = setInterval(() => {
      if (this.interacted || i >= text.length) {
        this.completeTyping();
        return;
      }
      i++;
      this.placeholder.set(text.slice(0, i));
    }, TYPE_INTERVAL_MS);
  }

  private completeTyping(): void {
    this.clearTypingInterval();
    if (this.targetPrompt) this.placeholder.set(this.targetPrompt);
  }

  private clearTypingInterval(): void {
    if (this.intervalId !== null) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }

  private prefersReducedMotion(): boolean {
    return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  }
}
