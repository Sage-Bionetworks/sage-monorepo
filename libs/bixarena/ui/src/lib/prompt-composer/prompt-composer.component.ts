import { Component, computed, ElementRef, input, output, signal, viewChild } from '@angular/core';
import { Popover, PopoverModule } from 'primeng/popover';

@Component({
  selector: 'bixarena-prompt-composer',
  imports: [PopoverModule],
  templateUrl: './prompt-composer.component.html',
  styleUrl: './prompt-composer.component.scss',
})
export class PromptComposerComponent {
  readonly placeholder = input('Ask anything biomedical...');
  readonly disabled = input(false);
  readonly maxLength = input.required<number>();

  readonly promptSubmit = output<string>();

  readonly text = signal('');
  readonly textareaEl = viewChild<ElementRef<HTMLTextAreaElement>>('textarea');
  private readonly disclaimerPopover = viewChild.required<Popover>('disclaimerPopover');
  readonly disclaimerOpen = signal(false);

  toggleDisclaimer(event: Event): void {
    this.disclaimerPopover().toggle(event);
  }

  readonly isHot = computed(() => this.text().trim().length > 0 && !this.disabled());

  onInput(event: Event): void {
    const el = event.target as HTMLTextAreaElement;
    this.text.set(el.value);
    el.style.height = 'auto';
    const max = parseFloat(getComputedStyle(el).maxHeight) || Infinity;
    el.style.height = `${Math.min(el.scrollHeight, max)}px`;
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.submit();
    }
  }

  submit(): void {
    const value = this.text().trim();
    if (!value || this.disabled()) return;
    this.promptSubmit.emit(value);
    this.text.set('');
    const el = this.textareaEl()?.nativeElement;
    if (el) {
      el.value = '';
      el.style.height = 'auto';
    }
  }
}
