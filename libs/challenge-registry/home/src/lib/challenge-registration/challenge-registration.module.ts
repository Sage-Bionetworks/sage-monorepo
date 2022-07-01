import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { ChallengeRegistrationComponent } from './challenge-registration.component';

@NgModule({
  declarations: [ChallengeRegistrationComponent],
  imports: [CommonModule, MatButtonModule],
  exports: [ChallengeRegistrationComponent],
})
export class ChallengeRegistrationModule {}
