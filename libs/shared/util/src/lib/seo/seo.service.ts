import { Injectable } from '@angular/core';
import { Meta, MetaDefinition, Title } from '@angular/platform-browser';
import { SeoData } from './seo-data';
import { SeoMetaType } from './seo-meta-type';

@Injectable({
  providedIn: 'root',
})
export class SeoService {
  constructor(private title: Title, private meta: Meta) {}

  setSeoData(seoData: SeoData): void {
    seoData.metas[SeoMetaType.TITLE] = {
      ...seoData.metas[SeoMetaType.TITLE],
      ...{
        name: 'title',
        property: 'og:title',
      },
    };
    seoData.metas[SeoMetaType.DESCRIPTION] = {
      ...seoData.metas[SeoMetaType.DESCRIPTION],
      ...{
        name: 'description',
        property: 'og:description',
      },
    };

    this.title.setTitle(seoData.title);
    this.meta.updateTag(seoData.metas[SeoMetaType.TITLE]);
    this.meta.updateTag(seoData.metas[SeoMetaType.DESCRIPTION]);
  }

  updateTitle(title: string) {
    this.title.setTitle(title);
  }

  updateMetaTags(metaTags: MetaDefinition[]) {
    metaTags.forEach((m) => {
      console.log('updateTag', m);
      this.meta.updateTag(m);
    });
  }
}
