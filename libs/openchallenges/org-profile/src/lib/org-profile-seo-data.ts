import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
import { SeoData } from '@sagebionetworks/shared/util';

export const getSeoData = (
  org: Organization,
  imageUrl: string | undefined
): SeoData => {
  return new SeoData({
    title: `${org.name} | OpenChallenges`,
    description: org.description,
    url: 'https://openchallenges.io',
    imageUrl:
      imageUrl ??
      'https://openchallenges.io/img/TOjdL9qUt-kphJRKO-iDvRV8KZE=/logo/OpenChallenges-icon.png',
    imageAlt: `${org.name} logo`,
    publishDate: '2023-09-20T00:00:00Z',
    jsonLds: [],
  });
};
