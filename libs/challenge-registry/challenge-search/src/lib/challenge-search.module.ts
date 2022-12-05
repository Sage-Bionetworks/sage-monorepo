import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { MatIconModule } from '@angular/material/icon';
import { ChallengeSearchComponent } from './challenge-search.component';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatLegacySelectModule as MatSelectModule } from '@angular/material/legacy-select';
import { MatLegacyCheckboxModule as MatCheckboxModule } from '@angular/material/legacy-checkbox';
import { MatExpansionModule } from '@angular/material/expansion';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

const routes: Routes = [{ path: '', component: ChallengeSearchComponent }];

@NgModule({
  imports: [
    CommonModule,
    MatIconModule,
    RouterModule.forChild(routes),
    UiModule,
    MatFormFieldModule,
    MatSelectModule,
    MatCheckboxModule,
    MatExpansionModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  declarations: [ChallengeSearchComponent],
  exports: [ChallengeSearchComponent],
})
export class ChallengeSearchModule {}
