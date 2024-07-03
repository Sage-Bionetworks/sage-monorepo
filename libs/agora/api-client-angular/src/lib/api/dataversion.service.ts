import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DataVersion } from '../model/dataversion';
import { BASE_URL, DEFAULT_HEADERS } from './constants';

@Injectable({
  providedIn: 'root',
})
export class DataVersionService {
  constructor(private http: HttpClient) {}

  getDataVersion(): Observable<DataVersion> {
    return this.http.get<DataVersion>(BASE_URL + '/dataversion', {
      headers: new HttpHeaders(DEFAULT_HEADERS),
    });
  }
}
