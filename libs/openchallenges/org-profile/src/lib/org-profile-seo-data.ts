import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
import { SeoData } from '@sagebionetworks/shared/util';
import {
  getDefaultSeoData,
  optimizeImageUrlForSeo,
} from '@sagebionetworks/openchallenges/util';

export const getSeoData = (
  org: Organization,
  imageUrl: string | undefined
): SeoData => {
  const defaultSeoData = getDefaultSeoData();

  return new SeoData({
    title: `${org.name} | OpenChallenges`,
    description: org.description,
    url: '',
    imageUrl:
      imageUrl !== undefined
        ? optimizeImageUrlForSeo(imageUrl)
        : defaultSeoData.imageUrl,
    imageAlt:
      imageUrl !== undefined ? `${org.name} logo` : defaultSeoData.imageAlt,
    publishDate: '2023-09-20T00:00:00Z', // TODO use org updatedAt?
    jsonLds: [],
  });
};
