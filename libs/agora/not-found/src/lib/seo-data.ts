import { SeoData } from '@sagebionetworks/shared/util';
import { getDefaultSeoData } from '@sagebionetworks/agora/util';

export const getSeoData = (): SeoData => {
  return Object.assign(getDefaultSeoData(), {
    title: 'Not Found | Agora',
  } as SeoData);
};
