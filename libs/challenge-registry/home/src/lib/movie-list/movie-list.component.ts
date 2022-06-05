import { Component, ViewChild } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTable } from '@angular/material/table';
import { KeycloakService } from 'keycloak-angular';
import { MovieListService } from './movie-list.service';
import { Movie } from './movie';

// interface Problem {
//   code: number;
//   reason: string;
//   timestamp: string;
//   message: string;
// }

@Component({
  selector: 'challenge-registry-movie-list',
  templateUrl: './movie-list.component.html',
  styleUrls: ['./movie-list.component.scss'],
})
export class MovieListComponent {
  movies: Movie[] = [];
  displayedColumns: string[] = ['title', 'director', 'year'];

  @ViewChild(MatTable) table!: MatTable<any>;

  constructor(
    private keycloakService: KeycloakService,
    private backend: MovieListService,
    private snackBar: MatSnackBar
  ) {}

  login() {
    this.keycloakService.login();
  }

  logout() {
    this.keycloakService.logout();
  }

  getAllMovies() {
    this.backend.getAllMovies().subscribe(
      (response) => {
        this.movies = response;
        this.table.renderRows();
      },
      (error) => {
        this.handleError(error.error);
      }
    );
  }

  onMovieIdChange(event: any) {
    this.getMovieById(event.value);
  }

  private getMovieById(id: number) {
    this.backend.getMovieById(id).subscribe(
      (response) => {
        this.movies = [response];
        this.table.renderRows();
      },
      (error) => {
        this.handleError(error.error);
      }
    );
  }

  private handleError(error: any) {
    this.displayError(error.code + ' ' + error.reason + '. ' + error.message);
  }

  private displayError(message: string) {
    this.snackBar.open(message, 'Close', { duration: 5000 });
  }
}
