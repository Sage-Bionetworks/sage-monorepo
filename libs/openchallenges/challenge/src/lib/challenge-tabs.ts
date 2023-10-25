import { Tab } from './tab.model';

export const CHALLENGE_TABS: { [key: string]: Tab } = {
  overview: {
    name: 'Overview',
    visible: true,
  },
  organizers: {
    name: 'Organizers',
    visible: true,
  },
  contributors: {
    name: 'Contributors',
    visible: true,
  },
  stargazers: {
    name: 'Stargazers',
    visible: true,
  },
};
