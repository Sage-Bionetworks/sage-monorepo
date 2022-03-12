import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { NgxTypedJsModule } from 'ngx-typed-js';
import { CountUpModule } from 'ngx-countup';
import { StatisticsViewerComponent } from './statistics-viewer.component';

@NgModule({
  declarations: [StatisticsViewerComponent],
  imports: [CommonModule, MatButtonModule, NgxTypedJsModule, CountUpModule],
  exports: [StatisticsViewerComponent],
})
export class StatisticsViewerModule {}
