import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './navbar/navbar.component';
import { ChallengeCardComponent } from './challenge-card/challenge-card.component';
import { CheckboxFilterComponent } from './checkbox-filter/checkbox-filter.component';
import { OrganizationCardComponent } from './organization-card/organization-card.component';
import { PaginatorComponent } from './paginator/paginator.component';
import { PersonCardComponent } from './person-card/person-card.component';
import { SearchDropdownFilterComponent } from './search-dropdown-filter/search-dropdown-filter.component';
import { UserButtonComponent } from './user-button/user-button.component';

@NgModule({
  imports: [
    CommonModule,
    NavbarComponent,
    ChallengeCardComponent,
    CheckboxFilterComponent,
    PaginatorComponent,
    OrganizationCardComponent,
    PersonCardComponent,
    SearchDropdownFilterComponent,
    UserButtonComponent,
  ],
  exports: [
    ChallengeCardComponent,
    CheckboxFilterComponent,
    PaginatorComponent,
    PersonCardComponent,
    NavbarComponent,
    OrganizationCardComponent,
    SearchDropdownFilterComponent,
    UserButtonComponent,
  ],
})
export class UiModule {}
