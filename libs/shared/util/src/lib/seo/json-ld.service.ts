import { DOCUMENT } from '@angular/common';
import { Inject, Injectable, Renderer2 } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class JsonLdService {
  private jsonLd: any = {};

  constructor(@Inject(DOCUMENT) private document: Document) {}

  /**
   * Set JSON-LD Microdata on the Document Body.
   *
   * @param renderer2 The Angular Renderer
   * @param data The data for the JSON-LD script
   * @returns Void
   */
  // public setJsonLd(renderer2: Renderer2, data: any): void {
  //   const script = renderer2.createElement('script');
  //   script.type = 'application/ld+json';
  //   script.text = `${JSON.stringify(data)}`;
  //   renderer2.appendChild(this._document.body, script);
  // }

  setData(renderer2: Renderer2, data: any) {
    const script = renderer2.createElement('script');
    script.type = 'application/ld+json';
    script.text = `${JSON.stringify(data)}`;
    renderer2.appendChild(this.document.body, script);
    // this.jsonLd = this.getObject(type, rawData);
  }

  getObject(type: string, rawData?: any) {
    let object = {
      '@context': 'http://schema.org',
      '@type': type,
    };
    if (rawData) {
      object = Object.assign({}, object, rawData);
    }
    return object;
  }

  toJson() {
    return JSON.stringify(this.jsonLd);
  }
}
