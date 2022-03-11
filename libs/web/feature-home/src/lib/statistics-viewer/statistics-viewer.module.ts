import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { NgxTypedJsModule } from 'ngx-typed-js';
import { StatisticsViewerComponent } from './statistics-viewer.component';

@NgModule({
  declarations: [StatisticsViewerComponent],
  imports: [CommonModule, MatButtonModule, NgxTypedJsModule],
  exports: [StatisticsViewerComponent],
})
export class StatisticsViewerModule {}
