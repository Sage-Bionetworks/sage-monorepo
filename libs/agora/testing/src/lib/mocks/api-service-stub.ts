/* eslint-disable */

import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

import { Distribution, Gene } from '@sagebionetworks/agora/api-client';
import { GCTGeneResponse, GenesResponse } from '@sagebionetworks/agora/models';
import { gctGeneMock1, geneMock1, geneMock2, nominatedGeneMock1, teamsResponseMock } from './';

import { TeamsList } from '@sagebionetworks/agora/api-client';

@Injectable()
export class ApiServiceStub {
  getGene(id: string): Observable<Gene | null> {
    return of(geneMock1);
  }

  getGenes(ids: string): Observable<GenesResponse> {
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
