import {
  Component,
  computed,
  DestroyRef,
  effect,
  inject,
  isDevMode,
  model,
  output,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { ModalDialogComponent } from '../modal-dialog/modal-dialog.component';

interface OnboardingFrame {
  readonly id: string;
  readonly title: string;
  readonly description: string;
  // Placeholder illustration; replaced once design lands.
  readonly art: string;
}

const AUTOPLAY_INTERVAL_MS = 2500;

@Component({
  selector: 'bixarena-onboarding-modal',
  imports: [ModalDialogComponent, ButtonModule],
  templateUrl: './onboarding-modal.component.html',
  styleUrl: './onboarding-modal.component.scss',
})
export class OnboardingModalComponent {
  readonly visible = model(false);
  readonly closed = output<void>();

  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly destroyRef = inject(DestroyRef);

  readonly frames: OnboardingFrame[] = [
    {
      id: 'stream',
      title: 'Two anonymous models answer',
      description:
        'You ask a biomedical question. Two AI models respond side-by-side, identities hidden so you compare answers, not brands.',
      art: 'A',
    },
    {
      id: 'vote',
      title: 'You pick the winner',
      description:
        'Read both answers. Vote for the one with clearer reasoning, better evidence, or sharper insight.',
      art: 'B',
    },
    {
      id: 'reveal',
      title: 'Models revealed, leaderboard updates',
      description:
        'The reveal shows which models you compared. Your vote feeds the live daily leaderboard ranking biomedical AI performance.',
      art: 'C',
    },
  ];

  readonly currentFrame = signal(0);
  readonly autoplayPaused = signal(false);
  readonly isLastFrame = computed(() => this.currentFrame() === this.frames.length - 1);

  private readonly reducedMotion = this.isBrowser
    ? window.matchMedia('(prefers-reduced-motion: reduce)').matches
    : false;

  private autoplayTimer: ReturnType<typeof setInterval> | null = null;

  constructor() {
    // Reset to first frame whenever the modal is opened so re-entry starts fresh.
    effect(() => {
      if (this.visible()) this.currentFrame.set(0);
    });

    effect(() => {
      const shouldRun =
        this.visible() && !this.autoplayPaused() && !this.reducedMotion && this.isBrowser;
      if (shouldRun) {
        this.startAutoplay();
      } else {
        this.stopAutoplay();
      }
    });

    this.destroyRef.onDestroy(() => this.stopAutoplay());
  }

  next(): void {
    this.currentFrame.update((i) => (i + 1) % this.frames.length);
  }

  prev(): void {
    this.currentFrame.update((i) => (i - 1 + this.frames.length) % this.frames.length);
  }

  goTo(index: number): void {
    if (index < 0 || index >= this.frames.length) {
      if (isDevMode()) console.warn('OnboardingModal: invalid frame index', index);
      return;
    }
    this.currentFrame.set(index);
  }

  done(): void {
    this.visible.set(false);
    this.closed.emit();
  }

  onModalClosed(): void {
    this.closed.emit();
  }

  private startAutoplay(): void {
    if (this.autoplayTimer !== null) return;
    this.autoplayTimer = setInterval(() => this.next(), AUTOPLAY_INTERVAL_MS);
  }

  private stopAutoplay(): void {
    if (this.autoplayTimer === null) return;
    clearInterval(this.autoplayTimer);
    this.autoplayTimer = null;
  }
}
