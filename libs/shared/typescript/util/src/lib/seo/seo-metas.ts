import { MetaDefinition } from '@angular/platform-browser';
import { SeoMetaId } from './seo-meta-id';

export interface SeoMetas {
  [SeoMetaId.AUTHOR]: MetaDefinition;
  [SeoMetaId.DESCRIPTION]: MetaDefinition;
  [SeoMetaId.IMAGE]: MetaDefinition;
  [SeoMetaId.PUBLISH_DATE]: MetaDefinition;
  [SeoMetaId.TITLE]: MetaDefinition;
  [SeoMetaId.TWITTER_CARD]: MetaDefinition;
  [SeoMetaId.TWITTER_DESCRIPTION]: MetaDefinition;
  [SeoMetaId.TWITTER_IMAGE_ALT]: MetaDefinition;
  [SeoMetaId.TWITTER_IMAGE]: MetaDefinition;
  [SeoMetaId.TWITTER_SITE]: MetaDefinition;
  [SeoMetaId.TWITTER_TITLE]: MetaDefinition;
  [SeoMetaId.TYPE]: MetaDefinition;
  [SeoMetaId.URL]: MetaDefinition;
}
