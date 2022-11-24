import { SeoData } from '@sagebionetworks/shared/util';
import { UserProfile } from './user-profile';

const getUserProfileSeoData = (userProfile: UserProfile): SeoData => {
  return {
    title: `${userProfile.username} - Challenge Registry`,
    metas: {
      AUTHOR: {
        content: `${userProfile.username}`,
      },
      // DESCRIPTION: {
      //   content: '',
      // },
      // IMAGE: {
      //   content: '',
      // },
      // PUBLISH_DATE: {
      //   content: '',
      // },
      TITLE: {
        content: `${userProfile.username} - Challenge Registry`,
      },
      TWITTER_TITLE: {
        content: `${userProfile.username} - Challenge Registry`,
      },
      // TWITTER_DESCRIPTION: {
      //   content: '',
      // },
      // TWITTER_IMAGE: {
      //   content: '',
      // },
      // TWITTER_IMAGE_ALT: {
      //   content: '',
      // },
      // TWITTER_SITE: {
      //   content: '',
      // },
      TYPE: {
        content: 'website',
      },
    },
  };
};

export { getUserProfileSeoData };
