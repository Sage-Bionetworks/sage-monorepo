import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { MatIconModule } from '@angular/material/icon';
import { ChallengeSearchComponent } from './challenge-search.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PanelModule } from 'primeng/panel';
import { RadioButtonModule } from 'primeng/radiobutton';
import { CalendarModule } from 'primeng/calendar';
import { DividerModule } from 'primeng/divider';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { MatSnackBarModule } from '@angular/material/snack-bar';

const routes: Routes = [{ path: '', component: ChallengeSearchComponent }];

@NgModule({
  imports: [
    CalendarModule,
    CommonModule,
    DividerModule,
    DropdownModule,
    InputTextModule,
    MatIconModule,
    MatSnackBarModule,
    RouterModule.forChild(routes),
    UiModule,
    FormsModule,
    PanelModule,
    RadioButtonModule,
    ReactiveFormsModule,
  ],
  declarations: [ChallengeSearchComponent],
  exports: [ChallengeSearchComponent],
})
export class ChallengeSearchModule {}
