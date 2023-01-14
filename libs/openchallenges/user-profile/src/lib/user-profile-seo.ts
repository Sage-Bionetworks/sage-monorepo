import { SeoData } from '@sagebionetworks/shared/util';
import { UserProfile } from './user-profile';

const getUserProfileSeoData = (userProfile: UserProfile): SeoData => {
  return {
    title: getTitle(userProfile),
    metas: {
      AUTHOR: {
        content: `${userProfile.username}`,
      },
      DESCRIPTION: {
        content: getDescription(userProfile),
      },
      IMAGE: {
        content: 'https://i.imgur.com/VeZfqCw.png',
      },
      // PUBLISH_DATE: {
      //   content: '',
      // },
      TITLE: {
        content: getTitle(userProfile),
      },
      TWITTER_TITLE: {
        content: getTitle(userProfile),
      },
      TWITTER_CARD: {
        content: 'summary_large_image',
      },
      TWITTER_DESCRIPTION: {
        content: getDescription(userProfile),
      },
      TWITTER_IMAGE: {
        content: 'https://i.imgur.com/VeZfqCw.png',
      },
      TWITTER_IMAGE_ALT: {
        content: `The avatar of the user ${userProfile.username}`,
      },
      // TWITTER_SITE: {
      //   content: '',
      // },
      TYPE: {
        content: 'website',
      },
      // URL: {
      //   content: url,
      // },
    },
  };
};

const getTitle = (userProfile: UserProfile): string => {
  return `${userProfile.username} - OpenChallenges`;
};

const getDescription = (userProfile: UserProfile): string => {
  return `The profile page of ${userProfile.username} on the OpenChallenges.`;
};

export { getUserProfileSeoData };
