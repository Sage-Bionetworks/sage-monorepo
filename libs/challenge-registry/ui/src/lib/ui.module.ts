import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonGithubModule } from './button-github/button-github.module';
import { FooterModule } from './footer/footer.module';
import { NavbarModule } from './navbar/navbar.module';
import { AvatarModule } from './avatar/avatar.module';
import { OrganizationCardModule } from './organization-card/organization-card.module';
import { UserButtonModule } from './user-button/user-button.module';
import { ChallengeCardComponent } from './challenge-card/challenge-card.component';

@NgModule({
  imports: [CommonModule],
  declarations: [ChallengeCardComponent],
  exports: [
    AvatarModule,
    ButtonGithubModule,
    FooterModule,
    NavbarModule,
    OrganizationCardModule,
    UserButtonModule,
  ],
})
export class UiModule {}
