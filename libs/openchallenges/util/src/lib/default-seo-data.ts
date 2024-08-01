import { SeoData } from '@sagebionetworks/shared/util';

export const getDefaultSeoData = (): SeoData => {
  return new SeoData({
    title: 'OpenChallenges: A Centralized Hub for Biomedical Challenges',
    description:
      'Data challenges have played a significant role in driving biomedical breakthroughs by engaging researchers, data scientists, and experts from various fields to collaborate on complex problems. The OpenChallenges initiative addresses current pain points like fragmented challenge information and lack of standardization. OpenChallenges.io is a centralized hub for biomedical challenges that empowers participants with the most up-to-date information about relevant challenges, while providing organizers with standardized challenge event templates and intelligence.',
    url: '',
    imageUrl: optimizeImageUrlForSeo(
      'https://openchallenges.io/img/SmLKBHMODG7ctWjEHwS1_jqGc7M=/0x500/logo/OpenChallenges-icon.svg',
    ),
    imageAlt: 'OpenChallenges logo',
    publishDate: '2023-09-20T00:00:00Z',
    jsonLds: [],
  });
};

// LinkedIn does not accept image URLs that end with 'svg' when generating rich cards. One way to
// trick LinkedIn is to happen a dummy query parameter. Our image server (Thumbor) ignores this
// parameter.
export const optimizeImageUrlForSeo = (imageUrl: string): string => {
  return imageUrl.endsWith('svg') ? `${imageUrl}?seo=true` : imageUrl;
};
