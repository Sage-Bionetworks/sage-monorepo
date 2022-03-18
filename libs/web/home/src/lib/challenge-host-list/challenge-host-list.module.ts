import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChallengeHostListComponent } from './challenge-host-list.component';

@NgModule({
  declarations: [ChallengeHostListComponent],
  imports: [CommonModule],
  exports: [ChallengeHostListComponent],
})
export class ChallengeHostListModule {}
