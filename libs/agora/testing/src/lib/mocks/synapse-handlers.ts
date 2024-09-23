import { http, HttpResponse } from 'msw';
import { synapseWikiMock } from '../synapse-mocks';

export const synapseHandlers = [
  http.get('https://repo-prod.prod.sagebase.org/repo/v1/entity/syn25913473/wiki', () => {
    return HttpResponse.json(synapseWikiMock);
  }),
];
