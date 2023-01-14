import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SponsorListComponent } from './sponsor-list.component';

@NgModule({
  declarations: [SponsorListComponent],
  imports: [CommonModule],
  exports: [SponsorListComponent],
})
export class SponsorListModule {}
