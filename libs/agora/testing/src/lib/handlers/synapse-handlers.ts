import { http, HttpResponse } from 'msw';
import { synapseWikiMarkdownMock } from '../mocks/synapse-mocks';

export const synapseHandlers = [
  http.get('https://repo-prod.prod.sagebase.org/repo/v1/entity/:ownerId/wiki/:wikiId', () => {
    return HttpResponse.json(synapseWikiMarkdownMock);
  }),
];
