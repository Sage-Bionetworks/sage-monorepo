import { inject, Injectable, Renderer2 } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { forIn } from 'lodash';
import { SeoData } from './seo-data';
import { SeoMetaId } from './seo-meta-id';
import { JsonLdService } from './json-ld.service';
import { SeoMetas } from './seo-metas';

// TODO Add support for the "canonical" links:
// <link rel="canonical" href="/datasets/nancyalaswad90/breast-cancer-dataset" />
@Injectable({
  providedIn: 'root',
})
export class SeoService {
  private titleService = inject(Title);
  private metaService = inject(Meta);
  private jsonLdService = inject(JsonLdService);

  setData(seoData: SeoData, renderer2: Renderer2): void {
    const title = seoData.title;
    const metas = this.buildMetas(seoData);
    const jsonLds = seoData.jsonLds;

    // Apply SEO data
    this.setTitle(title);
    this.setMetas(metas);
    this.setJsonLds(jsonLds, renderer2);
  }

  setTitle(title: string): void {
    this.titleService.setTitle(title);
  }

  setMetas(metas: SeoMetas): void {
    forIn(SeoMetaId, (id) => {
      if (metas[id]) {
        this.metaService.updateTag(metas[id]);
      }
    });
  }

  setJsonLds(jsonLds: object[], renderer2: Renderer2): void {
    jsonLds.forEach((j) => this.jsonLdService.addData(j, renderer2));
  }

  buildMetas(seoData: SeoData): SeoMetas {
    return {
      AUTHOR: {
        name: 'author',
        content: 'The OpenChallenges Team',
      },
      DESCRIPTION: {
        name: 'description',
        property: 'og:description',
        content: seoData.description,
      },
      IMAGE: {
        name: 'image',
        property: 'og:image',
        content: seoData.imageUrl,
      },
      PUBLISH_DATE: {
        name: 'publish_date',
        property: 'og:publish_date',
        content: seoData.publishDate,
      },
      TITLE: {
        name: 'title',
        property: 'og:title',
        content: seoData.title,
      },
      TWITTER_TITLE: {
        name: 'twitter:title',
        content: seoData.title,
      },
      TWITTER_CARD: {
        name: 'twitter:card',
        content: 'summary_large_image',
      },
      TWITTER_DESCRIPTION: {
        name: 'twitter:description',
        content: seoData.description,
      },
      TWITTER_IMAGE: {
        name: 'twitter:image',
        content: seoData.imageUrl,
      },
      TWITTER_IMAGE_ALT: {
        name: 'twitter:image:alt',
        content: seoData.imageAlt,
      },
      TWITTER_SITE: {
        name: 'twitter:site',
      },
      TYPE: {
        property: 'og:type',
        content: 'website',
      },
      URL: {
        name: 'og:url',
        content: seoData.url,
      },
    };
  }
}
