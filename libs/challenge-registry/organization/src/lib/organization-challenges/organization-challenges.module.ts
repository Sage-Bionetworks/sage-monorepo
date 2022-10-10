import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { OrganizationChallengesComponent } from './organization-challenges.component';

@NgModule({
  declarations: [OrganizationChallengesComponent],
  imports: [CommonModule, UiModule],
  exports: [OrganizationChallengesComponent],
})
export class OrganizationChallengesModule {}
