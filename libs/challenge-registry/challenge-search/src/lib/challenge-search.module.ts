import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { MatIconModule } from '@angular/material/icon';
import { ChallengeSearchComponent } from './challenge-search.component';
// import { MatFormFieldModule } from '@angular/material/form-field';
// import { MatSelectModule } from '@angular/material/select';
// import { MatCheckboxModule } from '@angular/material/checkbox';
// import { MatExpansionModule } from '@angular/material/expansion';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PanelModule } from 'primeng/panel';
import { RadioButtonModule } from 'primeng/radiobutton';
import { CalendarModule } from 'primeng/calendar';

const routes: Routes = [{ path: '', component: ChallengeSearchComponent }];

@NgModule({
  imports: [
    CalendarModule,
    CommonModule,
    MatIconModule,
    RouterModule.forChild(routes),
    UiModule,
    // MatFormFieldModule,
    // MatSelectModule,
    // MatCheckboxModule,
    // MatExpansionModule,
    FormsModule,
    PanelModule,
    RadioButtonModule,
    ReactiveFormsModule,
  ],
  declarations: [ChallengeSearchComponent],
  exports: [ChallengeSearchComponent],
})
export class ChallengeSearchModule {}
