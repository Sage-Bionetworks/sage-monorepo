import { DOCUMENT } from '@angular/common';
import { Inject, Injectable, Renderer2 } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class JsonLdService {
  constructor(@Inject(DOCUMENT) private document: Document) {}

  /**
   * Add JSON-LD data to the head of the document.
   *
   * @param renderer2 The Angular Renderer
   * @param data The data for the JSON-LD script
   * @returns {void}
   */
  addData(jsonLdData: any, renderer2: Renderer2): void {
    const script = renderer2.createElement('script');
    script.type = 'application/ld+json';
    script.text = `${JSON.stringify(jsonLdData)}`;
    renderer2.appendChild(this.document.head, script);
  }
}
