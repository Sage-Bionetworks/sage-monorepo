import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { MatIconModule } from '@angular/material/icon';
import { ChallengeSearchComponent } from './challenge-search.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
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
