import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChallengeCardComponent } from './challenge-card.component';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [ChallengeCardComponent],
  imports: [CommonModule, MatIconModule, RouterModule],
  exports: [ChallengeCardComponent],
})
export class ChallengeCardModule {}
