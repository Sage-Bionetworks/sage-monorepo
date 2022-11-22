import { MetaDefinition } from '@angular/platform-browser';
import { UserProfile } from './user-profile';

const getTitle = (userProfile: UserProfile): string => {
  return `${userProfile.username} - Challenge Registry`;
};

const getMetaTags = (userProfile: UserProfile): MetaDefinition[] => {
  return [
    {
      name: 'title',
      property: 'og:title',
      content: getTitle(userProfile),
    },
    {
      property: 'og:type',
      content: 'website',
    },
  ];
};

export { getTitle, getMetaTags };
