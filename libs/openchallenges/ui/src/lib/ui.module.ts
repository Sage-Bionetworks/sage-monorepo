import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChallengeCardModule } from './challenge-card/challenge-card.module';
import { OrganizationCardModule } from './organization-card/organization-card.module';
import { UserButtonModule } from './user-button/user-button.module';
import { PersonCardModule } from './person-card/person-card.module';
import { CheckboxFilterModule } from './checkbox-filter/checkbox-filter.module';
import { SearchDropdownFilterModule } from './search-dropdown-filter/search-dropdown-filter.module';
import { PaginatorModule } from './paginator/paginator.module';
import { NavbarComponent } from './navbar/navbar.component';

@NgModule({
  imports: [CommonModule, NavbarComponent],
  exports: [
    ChallengeCardModule,
    CheckboxFilterModule,
    PaginatorModule,
    PersonCardModule,
    NavbarComponent,
    OrganizationCardModule,
    SearchDropdownFilterModule,
    UserButtonModule,
  ],
})
export class UiModule {}
