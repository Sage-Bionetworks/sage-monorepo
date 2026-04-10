import { Component, computed, effect, ElementRef, input, signal, viewChild } from '@angular/core';
import { ModelStreamState } from '../battle.types';
import { STREAM_CURSOR } from '../battle.constants';

@Component({
  selector: 'bixarena-model-panel',
  templateUrl: './model-panel.component.html',
  styleUrl: './model-panel.component.scss',
})
export class ModelPanelComponent {
  readonly label = input.required<string>();
  readonly streamState = input.required<ModelStreamState>();
  readonly revealedName = input<string | null>(null);
  readonly isWinner = input(false);

  readonly bodyEl = viewChild<ElementRef<HTMLDivElement>>('body');
  private userScrolled = false;

  readonly displayText = computed(() => {
    const state = this.streamState();
    if (state.status === 'streaming') {
      return state.text + STREAM_CURSOR;
    }
    return state.text;
  });

  readonly statusClass = computed(() => {
    const status = this.streamState().status;
    if (status === 'streaming') return 'live';
    if (status === 'complete') return 'done';
    if (status === 'error') return 'error';
    return '';
  });

  readonly statusLabel = computed(() => {
    const state = this.streamState();
    if (state.status === 'waiting') return 'Connecting...';
    if (state.status === 'streaming' && state.isSlowHint) return 'Taking longer than expected...';
    if (state.status === 'streaming') return 'Generating...';
    if (state.status === 'complete') return 'Complete';
    if (state.status === 'error') return 'Error';
    return '';
  });

  constructor() {
    effect(() => {
      this.displayText();
      if (!this.userScrolled) {
        requestAnimationFrame(() => this.scrollToBottom());
      }
    });
  }

  onScroll(event: Event): void {
    const el = event.target as HTMLDivElement;
    const atBottom = el.scrollHeight - el.scrollTop - el.clientHeight < 40;
    this.userScrolled = !atBottom;
  }

  private scrollToBottom(): void {
    const el = this.bodyEl()?.nativeElement;
    if (el) {
      el.scrollTop = el.scrollHeight;
    }
  }
}
