import { Tab } from './tab.model';

export const ORGANIZATION_TABS: { [key: string]: Tab } = {
  overview: {
    name: 'Overview',
    visible: true,
  },
  challenges: {
    name: 'Challenges',
    visible: true,
  },
  starred: {
    name: 'Members',
    visible: true,
  },
};
