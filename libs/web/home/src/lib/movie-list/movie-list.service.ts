import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Movie } from './movie';

@Injectable({
  providedIn: 'root',
})
export class MovieListService {
  private readonly backendUrl = '/movies';

  constructor(private http: HttpClient) {}

  getAllMovies(): Observable<Array<Movie>> {
    return this.http.get<Array<Movie>>(this.backendUrl);
  }

  getMovieById(id: number): Observable<Movie> {
    return this.http.get<Movie>(this.backendUrl + '/' + id);
  }
}
