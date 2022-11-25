import { SeoData } from './seo-data';

const getBaseSeoData = (): SeoData => {
  return {
    title: 'Challenge Registry',
    metas: {
      AUTHOR: {
        name: 'author',
      },
      DESCRIPTION: {
        name: 'description',
        property: 'og:description',
      },
      IMAGE: {
        name: 'image',
        property: 'og:image',
      },
      PUBLISH_DATE: {
        name: 'publish_date',
        property: 'og:publish_date',
      },
      TITLE: {
        name: 'title',
        property: 'og:title',
      },
      TWITTER_TITLE: {
        name: 'twitter:title',
      },
      TWITTER_CARD: {
        name: 'twitter:card',
        content: 'summary_large_image',
      },
      TWITTER_DESCRIPTION: {
        name: 'twitter:description',
      },
      TWITTER_IMAGE: {
        name: 'twitter:image',
      },
      TWITTER_IMAGE_ALT: {
        name: 'twitter:image:alt',
      },
      TWITTER_SITE: {
        name: 'twitter:site',
      },
      TYPE: {
        property: 'og:type',
      },
      URL: {
        name: 'og:url',
      },
    },
  };
};

export { getBaseSeoData };
