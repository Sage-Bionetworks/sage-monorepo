import { Injectable } from '@angular/core';
import { Meta, MetaDefinition, Title } from '@angular/platform-browser';
import { getBaseSeoData } from './base-seo-data';
import { SeoData } from './seo-data';
import { SeoMetaType } from './seo-meta-type';
import { forIn } from 'lodash';

// TODO Add support for the "canonical" links:
// <link rel="canonical" href="/datasets/nancyalaswad90/breast-cancer-dataset" />
@Injectable({
  providedIn: 'root',
})
export class SeoService {
  private baseSeoData: SeoData;

  constructor(private title: Title, private meta: Meta) {
    this.baseSeoData = getBaseSeoData();
  }

  setData(seoData: SeoData): void {
    const title = seoData.title || this.baseSeoData.title;
    const metas: MetaDefinition[] = [];

    // only include the meta tags specified in seoData
    forIn(SeoMetaType, (value) => {
      if (seoData.metas[value]) {
        // the base metas are merged on top of the specified metas to ensure the proper values for
        // fields like `name` and `property`.
        metas.push({
          ...seoData.metas[value],
          ...this.baseSeoData.metas[value],
        });
      }
    });

    // TODO Validate SEO data

    this.setTitle(title);
    this.setMetaTags(metas);
  }

  setTitle(title: string) {
    this.title.setTitle(title);
  }

  setMetaTags(metaTags: MetaDefinition[]) {
    metaTags.forEach((m) => this.meta.updateTag(m));
  }
}
