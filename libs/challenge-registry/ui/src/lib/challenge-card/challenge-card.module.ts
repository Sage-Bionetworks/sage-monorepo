import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChallengeCardComponent } from './challenge-card.component';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [ChallengeCardComponent],
  imports: [CommonModule, MatIconModule],
  exports: [ChallengeCardComponent],
})
export class ChallengeCardModule {}
