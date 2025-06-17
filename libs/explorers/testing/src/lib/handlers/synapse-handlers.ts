import { http, HttpResponse } from 'msw';
import { synapseWikiMock } from '../mocks/synapse-mocks';
import { validWikiParams } from '../constants/wikiparams';
import { validMarkdown } from '../constants/markdown';

export const synapseHandlers = [
  // Get wiki markdown
  http.get(
    'https://repo-prod.prod.sagebase.org/repo/v1/entity/:ownerId/wiki/:wikiId',
    ({ params }) => {
      const wikiId = params['wikiId'] as string;
      const ownerId = params['ownerId'] as string;

      // Return mock data for valid IDs
      const isValid = validWikiParams.some(
        (param) => param.wikiId === wikiId && param.ownerId === ownerId,
      );
      if (isValid) {
        return HttpResponse.json({
          ...synapseWikiMock,
          markdown: validMarkdown,
        });
      }

      // Return 404 for invalid IDs
      return new HttpResponse(null, { status: 404 });
    },
  ),
];
