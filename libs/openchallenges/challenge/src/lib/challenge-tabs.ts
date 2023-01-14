import { Tab } from './tab.model';

export const CHALLENGE_TABS: { [key: string]: Tab } = {
  overview: {
    name: 'Overview',
    visible: true,
  },
  details: {
    name: 'Challenge Details',
    visible: true,
  },
  organizers: {
    name: 'Organizers',
    visible: true,
  },
  sponsors: {
    name: 'Sponsors',
    visible: true,
  },
  stargazers: {
    name: 'Stargazers',
    visible: true,
  },
};
