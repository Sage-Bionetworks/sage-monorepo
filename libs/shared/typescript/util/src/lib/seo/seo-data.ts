import { MetaDefinition } from '@angular/platform-browser';
import { SeoMetaType } from './seo-meta-type';

/** The data that a page should provide for SEO and social media sites. */
export interface SeoData {
  title: string;
  metas: {
    [SeoMetaType.AUTHOR]: MetaDefinition;
    [SeoMetaType.DESCRIPTION]: MetaDefinition;
    [SeoMetaType.IMAGE]: MetaDefinition;
    [SeoMetaType.PUBLISH_DATE]: MetaDefinition;
    [SeoMetaType.TITLE]: MetaDefinition;
    [SeoMetaType.TWITTER_CARD]: MetaDefinition;
    [SeoMetaType.TWITTER_DESCRIPTION]: MetaDefinition;
    [SeoMetaType.TWITTER_IMAGE_ALT]: MetaDefinition;
    [SeoMetaType.TWITTER_IMAGE]: MetaDefinition;
    [SeoMetaType.TWITTER_SITE]: MetaDefinition;
    [SeoMetaType.TWITTER_TITLE]: MetaDefinition;
    [SeoMetaType.TYPE]: MetaDefinition;
    [SeoMetaType.URL]: MetaDefinition;
  };
  jsonLds?: any[];
}
