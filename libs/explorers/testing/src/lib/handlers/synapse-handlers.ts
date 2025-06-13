import { http, HttpResponse } from 'msw';
import { synapseWikiMock } from '../mocks/synapse-mocks';

export const synapseHandlers = [
  http.get('https://repo-prod.prod.sagebase.org/repo/v1/entity/syn25913473/wiki', () => {
    return HttpResponse.json(synapseWikiMock);
  }),

  // Get wiki markdown
  http.get(
    'https://repo-prod.prod.sagebase.org/repo/v1/entity/:ownerId/wiki/:wikiId',
    ({ params }) => {
      const { ownerId, wikiId } = params;

      // Return mock data for valid IDs
      if (ownerId === 'syn25913473' && wikiId === '612058') {
        return HttpResponse.json({
          ...synapseWikiMock,
          markdown: '<h1>Test Wiki Content</h1><p>This is test content.</p>',
        });
      }

      // Return 404 for invalid IDs
      return new HttpResponse(null, { status: 404 });
    },
  ),
];
