import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonGithubModule } from './button-github/button-github.module';
import { ChallengeCardModule } from './challenge-card/challenge-card.module';
import { NavbarModule } from './navbar/navbar.module';
import { OrganizationCardModule } from './organization-card/organization-card.module';
import { UserButtonModule } from './user-button/user-button.module';
import { PersonCardModule } from './person-card/person-card.module';
import { CheckboxFilterModule } from './checkbox-filter/checkbox-filter.module';
import { SearchDropdownFilterModule } from './search-dropdown-filter/search-dropdown-filter.module';
import { PaginatorModule } from './paginator/paginator.module';

@NgModule({
  imports: [CommonModule],
  exports: [
    ButtonGithubModule,
    ChallengeCardModule,
    CheckboxFilterModule,
    PaginatorModule,
    PersonCardModule,
    NavbarModule,
    OrganizationCardModule,
    SearchDropdownFilterModule,
    UserButtonModule,
  ],
})
export class UiModule {}
