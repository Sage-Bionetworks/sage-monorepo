import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChallengeRegistrationComponent } from './challenge-registration.component';
import { MatButtonModule } from '@angular/material/button';

@NgModule({
  declarations: [ChallengeRegistrationComponent],
  imports: [CommonModule, MatButtonModule],
  exports: [ChallengeRegistrationComponent],
})
export class ChallengeRegistrationModule {}
