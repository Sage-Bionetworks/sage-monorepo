import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { NgxTypedJsModule } from 'ngx-typed-js';
import { CountUpModule } from 'ngx-countup';
import { StatisticsViewerComponent } from './statistics-viewer.component';
import * as echarts from 'echarts';
import { NgxEchartsModule } from 'ngx-echarts';

@NgModule({
  declarations: [StatisticsViewerComponent],
  imports: [
    CommonModule,
    MatButtonModule,
    NgxTypedJsModule,
    CountUpModule,
    NgxEchartsModule.forRoot({ echarts }),
  ],
  exports: [StatisticsViewerComponent],
})
export class StatisticsViewerModule {}
