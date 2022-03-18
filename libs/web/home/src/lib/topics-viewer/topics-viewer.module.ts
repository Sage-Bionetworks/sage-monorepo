import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TopicsViewerComponent } from './topics-viewer.component';

@NgModule({
  declarations: [TopicsViewerComponent],
  imports: [CommonModule],
  exports: [TopicsViewerComponent],
})
export class TopicsViewerModule {}
