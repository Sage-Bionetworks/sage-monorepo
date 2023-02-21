import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FooterComponent, UiModule } from '@sagebionetworks/openchallenges/ui';
import { MatIconModule } from '@angular/material/icon';
import { OrgSearchComponent } from './org-search.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PanelModule } from 'primeng/panel';
import { RadioButtonModule } from 'primeng/radiobutton';
import { CalendarModule } from 'primeng/calendar';
import { DividerModule } from 'primeng/divider';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { MatSnackBarModule } from '@angular/material/snack-bar';

const routes: Routes = [{ path: '', component: OrgSearchComponent }];

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
    FooterComponent,
  ],
  declarations: [OrgSearchComponent],
  exports: [OrgSearchComponent],
})
export class OrgSearchModule {}
