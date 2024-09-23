import { http, HttpResponse } from 'msw';
import { teamsResponseMock } from '../mocks/team-mocks';

export const teamHandlers = [
  http.get('/v1/teams', () => {
    return HttpResponse.json(teamsResponseMock);
  }),
];
