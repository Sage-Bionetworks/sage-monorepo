// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
// import { environment } from '../../../environments/environment';
import {
  Gene,
  GCTGeneResponse,
  GenesResponse,
  Distribution,
  BioDomainInfo,
  BioDomain,
  TeamsResponse,
} from '@sagebionetworks/agora/models';

// -------------------------------------------------------------------------- //
// Constants
// -------------------------------------------------------------------------- //
const defaultHeaders = {
  'Content-Type': 'application/json',
  'Cache-Control':
    'no-cache, no-store, must-revalidate, post-check=0, pre-check=0',
  Pragma: 'no-cache',
  Expires: '0',
};

// -------------------------------------------------------------------------- //
// Service
// -------------------------------------------------------------------------- //
@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private http: HttpClient) {}

  getBaseUrl() {
    const url = 'http://localhost:3333';

    // if (environment.api_host || environment.api_port) {
    //   const host = environment.api_host || window.location.hostname;
    //   const port = environment.api_port || window.location.port;
    //   url = '//' + host + ':' + port;
    // }

    return url;
  }

  getGene(id: string): Observable<Gene | null> {
    return this.http.get<Gene>(this.getBaseUrl() + '/v1/genes/' + id, {
      headers: new HttpHeaders(defaultHeaders),
    });
  }

  getGenes(ids: string | string[]): Observable<GenesResponse> {
    if (typeof ids === 'object') {
      ids = ids.join(',');
    }
    return this.http.get<GenesResponse>(this.getBaseUrl() + '/v1/genes', {
      headers: new HttpHeaders(defaultHeaders),
      params: new HttpParams().set('ids', ids),
    });
  }

  getBiodomain(ensg: string) {
    return this.http.get<BioDomain[]>(
      this.getBaseUrl() + '/v1/biodomains/' + ensg,
      {
        headers: new HttpHeaders(defaultHeaders),
      },
    );
  }

  getBiodomains() {
    return this.http.get<BioDomainInfo[]>(
      this.getBaseUrl() + '/v1/biodomains',
      {
        headers: new HttpHeaders(defaultHeaders),
      },
    );
  }

  searchGene(id: string): Observable<GenesResponse> {
    return this.http.get<GenesResponse>(
      this.getBaseUrl() + '/v1/genes/search',
      {
        headers: new HttpHeaders(defaultHeaders),
        params: new HttpParams().set('id', id),
      },
    );
  }

  getComparisonGenes(
    category: string,
    subCategory: string,
  ): Observable<GCTGeneResponse> {
    return this.http.get<GCTGeneResponse>(
      this.getBaseUrl() + '/v1/genes/comparison',
      {
        headers: new HttpHeaders(defaultHeaders),
        params: new HttpParams()
          .set('category', category)
          .set('subCategory', subCategory),
      },
    );
  }

  getNominatedGenes(): Observable<GenesResponse> {
    return this.http.get<GenesResponse>(
      this.getBaseUrl() + '/v1/genes/nominated',
      {
        headers: new HttpHeaders(defaultHeaders),
      },
    );
  }

  getDistribution(): Observable<Distribution> {
    return this.http.get<Distribution>(this.getBaseUrl() + '/v1/distribution', {
      headers: new HttpHeaders(defaultHeaders),
    });
  }

  getTeams(): Observable<TeamsResponse> {
    return this.http.get<TeamsResponse>(this.getBaseUrl() + '/v1/teams', {
      headers: new HttpHeaders(defaultHeaders),
    });
  }

  getTeamMemberImage(name: string): Observable<ArrayBuffer> {
    name = name.toLowerCase().replace(/[- ]/g, '-');
    return this.http.get(
      this.getBaseUrl() + '/v1/team-member/' + name + '/image',
      {
        headers: new HttpHeaders({
          'Content-Type': 'image/jpg, image/png, image/jpeg',
          Accept: 'image/jpg, image/png, image/jpeg',
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET',
          'Access-Control-Allow-Headers': 'Content-Type',
        }),
        responseType: 'arraybuffer',
      },
    );
  }
}
