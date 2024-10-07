/* eslint-disable */

import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

import { Gene, GenesResponse, GCTGeneResponse, Distribution } from '@sagebionetworks/agora/models';
import { geneMock1, geneMock2, gctGeneMock1, nominatedGeneMock1, teamsResponseMock } from './';

import { TeamsList } from '@sagebionetworks/agora/api-client-angular';

@Injectable()
export class ApiServiceStub {
  getGene(id: string): Observable<Gene | null> {
    return of(geneMock1);
  }

  getGenes(ids: string | string[]): Observable<GenesResponse> {
    return of({ items: [geneMock1, geneMock2] });
  }

  searchGene(id: string): Observable<GenesResponse> {
    return of({ items: [geneMock1, geneMock2] });
  }

  getComparisonGenes(category: string, subCategory: string): Observable<GCTGeneResponse> {
    return of({ items: [gctGeneMock1] });
  }

  getNominatedGenes(): Observable<GenesResponse> {
    return of({ items: [nominatedGeneMock1] });
  }

  getDistribution(): Observable<Distribution> {
    return of({} as Distribution);
  }

  getTeams(): Observable<TeamsList> {
    return of(teamsResponseMock);
  }

  getTeamMemberImage(name: string): Observable<ArrayBuffer> {
    return of(new ArrayBuffer(0));
  }
}
