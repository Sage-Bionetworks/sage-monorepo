import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { NgxTypedJsModule } from 'ngx-typed-js';
import { SearchViewerComponent } from './search-viewer.component';

@NgModule({
  declarations: [SearchViewerComponent],
  imports: [CommonModule, MatButtonModule, NgxTypedJsModule],
  exports: [SearchViewerComponent],
})
export class SearchViewerModule {}
