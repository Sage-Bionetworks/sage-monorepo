import { getDefaultSeoData } from '@sagebionetworks/openchallenges/util';
import { SeoData } from '@sagebionetworks/shared/util';

export const getSeoData = (): SeoData => {
  return getDefaultSeoData();
};
