import { MetaDefinition } from '@angular/platform-browser';
import { SeoMetaType } from './seo-meta-type';

export interface SeoData {
  title: string;
  metas: {
    [SeoMetaType.TITLE]: MetaDefinition;
    [SeoMetaType.DESCRIPTION]: MetaDefinition;
  };
}
