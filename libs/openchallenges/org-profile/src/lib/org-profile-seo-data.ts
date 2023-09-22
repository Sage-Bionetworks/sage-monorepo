import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
import { SeoData } from '@sagebionetworks/shared/util';
import { getSeoData as getHomeSeoData } from '@sagebionetworks/openchallenges/home';

export const getSeoData = (
  org: Organization,
  imageUrl: string | undefined
): SeoData => {
  return new SeoData({
    title: `${org.name} | OpenChallenges`,
    description: org.description,
    url: '',
    imageUrl: imageUrl ?? getHomeSeoData().imageUrl,
    imageAlt:
      imageUrl !== undefined ? `${org.name} logo` : getHomeSeoData().imageAlt,
    publishDate: '2023-09-20T00:00:00Z', // TODO use org updatedAt?
    jsonLds: [],
  });
};
