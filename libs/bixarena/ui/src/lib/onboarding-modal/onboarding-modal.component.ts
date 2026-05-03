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
}

const AUTOPLAY_INTERVAL_MS = 7000;
const SAMPLE_PROMPT = 'How does sleep affect the immune system?';

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

  readonly samplePrompt = SAMPLE_PROMPT;

  readonly frames: OnboardingFrame[] = [
    {
      id: 'start',
      title: 'Start a Battle',
      description:
        'Pick a curated example or ask your own biomedical question. Two AI models are randomly chosen to face off, and their identities stay anonymous so you can focus purely on the response quality.',
    },
    {
      id: 'select',
      title: 'Select the Better',
      description:
        'Review the two AI-generated answers side by side and pick the one with clearer reasoning or sharper insight. After you vote, both models are revealed.',
    },
    {
      id: 'reveal',
      title: 'Make Your Impact',
      description:
        'Only biomedical battles count toward the daily leaderboard. Ready for another round? Jump into your next battle with the same question against new models, or start a fresh battle.',
    },
  ];

  readonly currentFrame = signal(0);
  readonly autoplayPaused = signal(false);
  readonly isFirstFrame = computed(() => this.currentFrame() === 0);
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
      // No autoplay on last frame: wrap would scroll backward through prior slides.
      const shouldRun =
        this.visible() &&
        !this.autoplayPaused() &&
        !this.reducedMotion &&
        this.isBrowser &&
        !this.isLastFrame();
      if (shouldRun) {
        this.startAutoplay();
      } else {
        this.stopAutoplay();
      }
    });

    this.destroyRef.onDestroy(() => this.stopAutoplay());
  }

  next(): void {
    if (this.isLastFrame()) return;
    this.currentFrame.update((i) => i + 1);
  }

  prev(): void {
    if (this.isFirstFrame()) return;
    this.currentFrame.update((i) => i - 1);
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
