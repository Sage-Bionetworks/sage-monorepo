import { Component, ElementRef, input, output, signal, viewChild } from '@angular/core';

@Component({
  selector: 'bixarena-prompt-composer',
  templateUrl: './prompt-composer.component.html',
  styleUrl: './prompt-composer.component.scss',
})
export class PromptComposerComponent {
  readonly placeholder = input('Ask anything biomedical...');
  readonly disabled = input(false);
  readonly maxLength = input(5000);

  readonly promptSubmit = output<string>();

  readonly text = signal('');
  readonly textareaEl = viewChild<ElementRef<HTMLTextAreaElement>>('textarea');

  get isHot(): boolean {
    return this.text().trim().length > 0 && !this.disabled();
  }

  onInput(event: Event): void {
    const el = event.target as HTMLTextAreaElement;
    this.text.set(el.value);
    el.style.height = 'auto';
    el.style.height = `${el.scrollHeight}px`;
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
