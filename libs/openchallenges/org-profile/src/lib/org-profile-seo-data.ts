import { Organization } from '@sagebionetworks/openchallenges/api-client';
import { SeoData } from '@sagebionetworks/shared/util';
import { getDefaultSeoData, optimizeImageUrlForSeo } from '@sagebionetworks/openchallenges/util';

export const getSeoData = (org: Organization, imageUrl: string | undefined): SeoData => {
  const defaultSeoData = getDefaultSeoData();
  // shallow merge
  return Object.assign(defaultSeoData, {
    title: `${org.name} | OpenChallenges`,
    description: org.description,
    imageUrl: imageUrl !== undefined ? optimizeImageUrlForSeo(imageUrl) : defaultSeoData.imageUrl,
    imageAlt: imageUrl !== undefined ? `${org.name} logo` : defaultSeoData.imageAlt,
  } as SeoData);
};
