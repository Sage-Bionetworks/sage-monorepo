import { SeoData } from '@sagebionetworks/shared/util';
import { UserProfile } from './user-profile';

const getUserProfileSeoData = (userProfile: UserProfile): SeoData => {
  return {
    title: `${userProfile.username} - Challenge Registry`,
    metas: {
      TITLE: {
        content: `${userProfile.username} - Challenge Registry`,
      },
      DESCRIPTION: {
        content: '',
      },
    },
  };
};

export { getUserProfileSeoData };
