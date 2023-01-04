import { Injectable, Renderer2 } from '@angular/core';
import { Meta, MetaDefinition, Title } from '@angular/platform-browser';
import { getBaseSeoData } from './base-seo-data';
import { SeoData } from './seo-data';
import { SeoMetaType } from './seo-meta-type';
import { forIn } from 'lodash';
import { JsonLdService } from './json-ld.service';

// TODO Add support for the "canonical" links:
// <link rel="canonical" href="/datasets/nancyalaswad90/breast-cancer-dataset" />
@Injectable({
  providedIn: 'root',
})
export class SeoService {
  private baseSeoData: SeoData;

  constructor(
    private title: Title,
    private meta: Meta,
    private jsonLdService: JsonLdService
  ) {
    this.baseSeoData = getBaseSeoData();
  }

  setData(seoData: SeoData, renderer2: Renderer2): void {
    const title = seoData.title || this.baseSeoData.title;
    const metas: MetaDefinition[] = [];
    const jsonLds: any[] = [];

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

    // TODO Validate metas

    forIn(seoData.jsonLds, (jsonLd) => {
      // TODO validate the json ld object
      jsonLds.push(jsonLd);
    });

    this.setTitle(title);
    this.setMetas(metas);
    this.addJsonLds(jsonLds, renderer2);
  }

  setTitle(title: string) {
    this.title.setTitle(title);
  }

  setMetas(metas: MetaDefinition[]) {
    metas.forEach((m) => this.meta.updateTag(m));
  }

  addJsonLds(jsonLds: any[], renderer2: Renderer2): void {
    jsonLds.forEach((j) => this.jsonLdService.addData(j, renderer2));
  }
}
