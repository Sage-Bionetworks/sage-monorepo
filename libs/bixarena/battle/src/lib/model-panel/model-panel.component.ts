import {
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  input,
  PLATFORM_ID,
  viewChild,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { MarkdownComponent } from 'ngx-markdown';
import { BattleEvaluationOutcome } from '@sagebionetworks/bixarena/api-client';
import { ModelStreamState } from '../battle.types';
import { STREAM_CURSOR } from '../battle.constants';

@Component({
  selector: 'bixarena-model-panel',
  imports: [MarkdownComponent],
  templateUrl: './model-panel.component.html',
  styleUrl: './model-panel.component.scss',
})
export class ModelPanelComponent {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly modelId = input.required<'model1' | 'model2'>();
  readonly anonymousName = computed(() => (this.modelId() === 'model1' ? 'Model A' : 'Model B'));
  readonly streamState = input.required<ModelStreamState>();
  readonly modelName = input<string | null>(null);
  readonly selectedOutcome = input<BattleEvaluationOutcome | null>(null);
  readonly hoveredModel = input<BattleEvaluationOutcome | null>(null);

  readonly isSelected = computed(() => {
    const o = this.selectedOutcome();
    if (!o) return false;
    return o === this.modelId() || o === 'tie';
  });

  readonly isUnselected = computed(() => {
    const o = this.selectedOutcome();
    if (!o) return false;
    return o !== this.modelId() && o !== 'tie';
  });

  readonly isHovered = computed(() => {
    const h = this.hoveredModel();
    return h === this.modelId() || h === 'tie';
  });

  readonly bodyEl = viewChild<ElementRef<HTMLDivElement>>('body');
  private isUserScrolled = false;
  private isAutoScroll = false;

  readonly displayText = computed(() => {
    const state = this.streamState();
    if (state.status === 'streaming') {
      return state.text + STREAM_CURSOR;
    }
    return state.text;
  });

  constructor() {
    if (this.isBrowser) {
      // Auto-scroll to bottom on every stream state change, unless user has scrolled up
      effect(() => {
        this.streamState(); // Read to register signal dependency
        if (!this.isUserScrolled) {
          requestAnimationFrame(() => this.scrollToBottom());
        }
      });
    }
  }

  // Detect manual scroll: if user scrolls away from bottom, stop auto-scrolling
  onScroll(event: Event): void {
    if (this.isAutoScroll) return;
    const el = event.target as HTMLDivElement;
    const atBottom = el.scrollHeight - el.scrollTop - el.clientHeight < 40;
    this.isUserScrolled = !atBottom;
  }

  private scrollToBottom(): void {
    const el = this.bodyEl()?.nativeElement;
    if (el) {
      // Flag prevents onScroll from treating our scrollTo as a user-initiated scroll
      this.isAutoScroll = true;
      el.scrollTop = el.scrollHeight;
      requestAnimationFrame(() => (this.isAutoScroll = false));
    }
  }
}
