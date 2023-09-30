import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular';
import { SeoData } from '@sagebionetworks/shared/util';
import { getDefaultSeoData } from '@sagebionetworks/openchallenges/util';

export const getSeoData = (challenge: Challenge): SeoData => {
  const defaultSeoData = getDefaultSeoData();
  // shallow merge
  return Object.assign(defaultSeoData, {
    title: `${challenge.name} | OpenChallenges`,
    description: challenge.description,
  } as SeoData);
};
