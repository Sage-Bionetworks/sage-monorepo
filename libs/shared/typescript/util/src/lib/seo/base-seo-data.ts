import { SeoData } from './seo-data';

const getBaseSeoData = (): SeoData => {
  return {
    title: getTitle(),
    metas: {
      AUTHOR: {
        name: 'author',
        content: 'The OpenChallenges Team',
      },
      DESCRIPTION: {
        name: 'description',
        property: 'og:description',
        content: getDescription(),
      },
      IMAGE: {
        name: 'image',
        property: 'og:image',
        content: getImage(),
      },
      PUBLISH_DATE: {
        name: 'publish_date',
        property: 'og:publish_date',
        content: '2023-09-20T00:00:00Z',
      },
      TITLE: {
        name: 'title',
        property: 'og:title',
        content: getTitle(),
      },
      TWITTER_TITLE: {
        name: 'twitter:title',
        content: getTitle(),
      },
      TWITTER_CARD: {
        name: 'twitter:card',
        content: 'summary_large_image',
      },
      TWITTER_DESCRIPTION: {
        name: 'twitter:description',
        content: getDescription(),
      },
      TWITTER_IMAGE: {
        name: 'twitter:image',
        content: getImage(),
      },
      TWITTER_IMAGE_ALT: {
        name: 'twitter:image:alt',
        content: 'OpenChallenges logo',
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
        content: 'https://openchallenges.io',
      },
    },
    jsonLds: [],
  };
};

const getTitle = (): string => {
  return 'OpenChallenges: A centralized hub for biomedical challenges and more';
};

const getDescription = (): string => {
  return 'OpenChallenges is a centralized hub for biomedical challenges and more.';
};

const getImage = (): string => {
  return 'https://openchallenges.io/openchallenges-assets/images/openchallenges-color.svg';
};

export { getBaseSeoData };
