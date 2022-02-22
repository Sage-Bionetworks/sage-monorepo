import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageTitleModule } from './page-title/page-title.module';

@NgModule({
  imports: [CommonModule],
  exports: [PageTitleModule],
})
export class WebDataAccessModule {}
