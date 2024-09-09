import { getDefaultSeoData } from '@sagebionetworks/openchallenges/util';
import { SeoData } from '@sagebionetworks/shared/util';

export const getSeoData = (): SeoData => {
  // shallow merge
  return Object.assign(getDefaultSeoData(), {
    title: 'The OpenChallenges Team',
    description: "We're smart, we're hard-working, and we love a good challenge.",
  } as SeoData);
};
