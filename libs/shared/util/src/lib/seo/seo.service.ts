import { Injectable } from '@angular/core';
import { Meta, MetaDefinition, Title } from '@angular/platform-browser';
import { getBaseSeoData } from './base-seo-data';
import { SeoData } from './seo-data';
import { SeoMetaType } from './seo-meta-type';
import { forIn } from 'lodash';

@Injectable({
  providedIn: 'root',
})
export class SeoService {
  private baseSeoData: SeoData;

  constructor(private title: Title, private meta: Meta) {
    this.baseSeoData = getBaseSeoData();
  }

  updateSeoData(seoData: SeoData): void {
    const title = seoData.title;
    const metas: MetaDefinition[] = [];

    forIn(SeoMetaType, (value) => {
      if (seoData.metas[value]) {
        metas.push({
          ...seoData.metas[value],
          ...this.baseSeoData.metas[value],
        });
      }
    });

    this.updateTitle(title);
    this.updateMetaTags(metas);
  }

  updateTitle(title: string) {
    this.title.setTitle(title);
  }

  updateMetaTags(metaTags: MetaDefinition[]) {
    metaTags.forEach((m) => this.meta.updateTag(m));
  }
}
