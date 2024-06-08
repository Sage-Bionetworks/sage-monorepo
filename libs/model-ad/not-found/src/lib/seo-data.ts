import { SeoData } from '@sagebionetworks/shared/util';
import { getDefaultSeoData } from '@sagebionetworks/model-ad/util';

export const getSeoData = (): SeoData => {
  return Object.assign(getDefaultSeoData(), {
    title: 'Not Found | MODEL-AD',
  } as SeoData);
};
