import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  OnInit,
  PLATFORM_ID,
  signal,
  viewChild,
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
const PROMPT_POOL_SIZE = 5;
const TYPE_INTERVAL_MS = 45;
const ERASE_INTERVAL_MS = 12;
const HOLD_MS = 3500;
const BETWEEN_PROMPT_MS = 500;
const RESUME_AFTER_BLUR_MS = 600;
const REDUCED_MOTION_ROTATE_MS = 5000;

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
  readonly composer = viewChild(PromptComposerComponent);

  private timeoutId: ReturnType<typeof setTimeout> | null = null;
  private prompts: string[] = [];
  private currentIndex = 0;

  ngOnInit(): void {
    if (!this.isBrowser) return;

    const query: ExamplePromptSearchQuery = {
      sort: ExamplePromptSort.Random,
      pageSize: PROMPT_POOL_SIZE,
      active: true,
    };
    this.examplePrompts
      .listExamplePrompts(query)
      .pipe(
        catchError(() => of(null)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((page) => {
        const prompts = (page?.examplePrompts ?? []).map((p) => p.question).filter(Boolean);
        if (prompts.length === 0) return;
        this.prompts = prompts;
        this.resumeAnimation();
      });

    this.destroyRef.onDestroy(() => this.clearTimer());
  }

  // On focus, show the static "Ask anything..." placeholder so the user gets
  // a clean "your turn" signal instead of a half-typed prompt.
  onComposerFocus(): void {
    this.clearTimer();
    this.placeholder.set(STATIC_PLACEHOLDER);
  }

  // On blur, if the user didn't type anything, advance to the next prompt
  // and resume the rotation after a brief delay. The delay lets quick
  // re-focuses (e.g. focus moving within the section) cancel the resume.
  onComposerBlur(): void {
    this.clearTimer();
    this.timeoutId = setTimeout(() => {
      if (this.composer()?.text()?.length || this.prompts.length === 0) return;
      this.currentIndex = (this.currentIndex + 1) % this.prompts.length;
      this.resumeAnimation();
    }, RESUME_AFTER_BLUR_MS);
  }

  onPromptSubmit(prompt: string): void {
    this.gate.savePendingPrompt(prompt);
    if (this.auth.isAuthenticated()) {
      void this.router.navigate(['/battle']);
    } else {
      this.gate.showLoginModal.set(true);
    }
  }

  private resumeAnimation(): void {
    if (this.prompts.length === 0) return;
    if (this.prefersReducedMotion()) {
      this.placeholder.set(this.prompts[this.currentIndex]);
      this.scheduleReducedMotionRotate();
    } else {
      this.typeNext();
    }
  }

  private typeNext(): void {
    const text = this.prompts[this.currentIndex];
    this.placeholder.set('');
    this.typeChar(text, 0);
  }

  private typeChar(text: string, i: number): void {
    if (i >= text.length) {
      this.timeoutId = setTimeout(() => this.eraseChar(text, text.length), HOLD_MS);
      return;
    }
    this.placeholder.set(text.slice(0, i + 1));
    this.timeoutId = setTimeout(() => this.typeChar(text, i + 1), TYPE_INTERVAL_MS);
  }

  private eraseChar(text: string, i: number): void {
    if (i <= 0) {
      this.currentIndex = (this.currentIndex + 1) % this.prompts.length;
      this.timeoutId = setTimeout(() => this.typeNext(), BETWEEN_PROMPT_MS);
      return;
    }
    this.placeholder.set(text.slice(0, i - 1));
    this.timeoutId = setTimeout(() => this.eraseChar(text, i - 1), ERASE_INTERVAL_MS);
  }

  private scheduleReducedMotionRotate(): void {
    this.timeoutId = setTimeout(() => {
      this.currentIndex = (this.currentIndex + 1) % this.prompts.length;
      this.placeholder.set(this.prompts[this.currentIndex]);
      this.scheduleReducedMotionRotate();
    }, REDUCED_MOTION_ROTATE_MS);
  }

  private clearTimer(): void {
    if (this.timeoutId !== null) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }
  }

  private prefersReducedMotion(): boolean {
    return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  }
}
