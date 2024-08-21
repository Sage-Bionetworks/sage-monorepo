import { SeoData } from '@sagebionetworks/shared/util';

export const getDefaultSeoData = (): SeoData => {
  return new SeoData({
    title: 'Agora',
    description: 'A description',
    url: '',
    imageUrl: '',
    imageAlt: 'Agora logo',
    publishDate: '2023-09-20T00:00:00Z',
    jsonLds: [],
  });
};
