import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { SearchViewerComponent } from './search-viewer.component';

@NgModule({
  declarations: [SearchViewerComponent],
  imports: [CommonModule, MatButtonModule],
  exports: [SearchViewerComponent],
})
export class SearchViewerModule {}
