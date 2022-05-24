import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MovieListComponent } from './movie-list.component';
import { MovieListService } from './movie-list.service';

@NgModule({
  declarations: [MovieListComponent],
  imports: [CommonModule, MatSnackBarModule, MatTableModule],
  providers: [MovieListService],
  exports: [MovieListComponent],
})
export class MovieListModule {}
