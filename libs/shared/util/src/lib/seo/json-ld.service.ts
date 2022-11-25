import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class JsonLdService {
  private jsonLd: any = {};

  setData(type: string, rawData: any) {
    this.jsonLd = this.getObject(type, rawData);
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
