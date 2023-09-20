import { SeoData } from '@sagebionetworks/shared/util';

export const homeSeoData = (): SeoData => {
  return {
    title: '', // leave empty to use the default title
    metas: {
      AUTHOR: {}, // use empty object to use the default meta
      DESCRIPTION: {},
      IMAGE: {},
      PUBLISH_DATE: {},
      TITLE: {},
      TWITTER_CARD: {},
      TWITTER_DESCRIPTION: {},
      TWITTER_IMAGE_ALT: {},
      TWITTER_IMAGE: {},
      TWITTER_SITE: {},
      TWITTER_TITLE: {},
      TYPE: {},
      URL: {},
    },
  };
};
