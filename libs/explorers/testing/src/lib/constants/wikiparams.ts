import { SynapseWikiParams } from '@sagebionetworks/explorers/models';

export const validWikiParams: SynapseWikiParams[] = [
  {
    ownerId: 'syn66271427',
    wikiId: '631750',
  },
  {
    ownerId: 'syn66271427',
    wikiId: '631751',
  },
];

export const invalidWikiParam: SynapseWikiParams = {
  ownerId: 'syn99999999',
  wikiId: '999999',
};
