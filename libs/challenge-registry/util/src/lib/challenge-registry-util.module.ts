import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageTitleService } from './page-title/page-title.service';

@NgModule({
  imports: [CommonModule],
  providers: [PageTitleService],
})
export class ChallengeRegistryUtilModule {}
