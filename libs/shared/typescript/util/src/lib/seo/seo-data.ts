import { SeoDataOptions } from './seo-data-options';

export class SeoData implements SeoDataOptions {
  title: string;
  description: string;
  url: string;
  imageUrl: string;
  imageAlt: string;
  publishDate: string;
  jsonLds: object[];

  constructor(options: SeoDataOptions) {
    this.title = options.title;
    this.description = options.description;
    this.url = options.url;
    this.imageUrl = options.imageUrl;
    this.imageAlt = options.imageAlt;
    this.publishDate = options.publishDate;
    this.jsonLds = options.jsonLds;
  }
}
