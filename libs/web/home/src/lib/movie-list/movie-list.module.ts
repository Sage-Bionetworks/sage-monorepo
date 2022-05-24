import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MovieListComponent } from './movie-list.component';

@NgModule({
  declarations: [MovieListComponent],
  imports: [CommonModule],
  exports: [MovieListComponent],
})
export class MovieListModule {}
