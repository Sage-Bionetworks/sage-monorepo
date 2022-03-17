import { Tab } from './tab.model';

export const USER_PROFILE_TABS: { [key: string]: Tab } = {
  overview: {
    name: 'Overview',
    visible: true,
  },
  challenges: {
    name: 'Challenges',
    visible: true,
  },
  starred: {
    name: 'Starred',
    visible: false,
  },
};
