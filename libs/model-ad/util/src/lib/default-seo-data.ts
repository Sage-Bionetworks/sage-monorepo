import { SeoData } from '@sagebionetworks/shared/util';

export const getDefaultSeoData = (): SeoData => {
  return new SeoData({
    title: 'MODEL-AD',
    description:
      'A complementary team of investigators from several laboratories and institutions',
    url: '',
    imageUrl: '',
    imageAlt: 'MODEL-AD logo',
    publishDate: '2023-09-20T00:00:00Z',
    jsonLds: [],
  });
};
