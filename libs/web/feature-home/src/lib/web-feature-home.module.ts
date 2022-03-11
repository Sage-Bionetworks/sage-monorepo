import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchViewerModule } from './search-viewer/search-viewer.module';
import { StatisticsViewerComponent } from './statistics-viewer/statistics-viewer.component';

@NgModule({
  imports: [CommonModule],
  declarations: [StatisticsViewerComponent],
  exports: [SearchViewerModule],
})
export class WebFeatureHomeModule {}
