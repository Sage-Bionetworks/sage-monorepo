import { afterNextRender, Directive, ElementRef, inject, OnDestroy } from '@angular/core';

// Components using this directive must read var(--widest-line-width) in their styles, with a
// fallback for before the first measurement.
export const WIDEST_LINE_WIDTH_PROPERTY = '--widest-line-width';

/**
 * Exposes the width of the host's widest rendered line of text as a CSS custom property, so styles
 * can size decorations (underlines, highlights) to the text rather than to the full element box.
 */
@Directive({
  selector: '[explorersWidestLineWidth]',
})
export class WidestLineWidthDirective implements OnDestroy {
  private readonly element = inject<ElementRef<HTMLElement>>(ElementRef).nativeElement;

  private resizeObserver?: ResizeObserver;

  constructor() {
    afterNextRender(() => {
      this.resizeObserver = new ResizeObserver(() => this.updateWidestLineWidth());
      this.resizeObserver.observe(this.element);
      // A web font swap changes text metrics without necessarily resizing the host element.
      document.fonts?.ready.then(() => this.updateWidestLineWidth());
    });
  }

  ngOnDestroy() {
    this.resizeObserver?.disconnect();
  }

  private updateWidestLineWidth() {
    const lineWidths = this.getTextLineWidths();
    if (lineWidths.length === 0) return;

    this.element.style.setProperty(
      WIDEST_LINE_WIDTH_PROPERTY,
      `${Math.ceil(Math.max(...lineWidths))}px`,
    );
  }

  private getTextLineWidths(): number[] {
    // Ranges over text nodes yield one rect per rendered line; a range over the host would instead
    // yield the box of any block-level child, which is as wide as the host.
    const textNodes = document.createTreeWalker(this.element, NodeFilter.SHOW_TEXT);
    const textRange = document.createRange();
    const lineWidths: number[] = [];

    for (let textNode = textNodes.nextNode(); textNode; textNode = textNodes.nextNode()) {
      textRange.selectNodeContents(textNode);
      lineWidths.push(...Array.from(textRange.getClientRects(), (rect) => rect.width));
    }

    return lineWidths;
  }
}
