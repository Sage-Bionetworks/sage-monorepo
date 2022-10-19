import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonGithubModule } from './button-github/button-github.module';
import { ChallengeCardModule } from './challenge-card/challenge-card.module';
import { FooterModule } from './footer/footer.module';
import { NavbarModule } from './navbar/navbar.module';
import { AvatarModule } from './avatar/avatar.module';
import { OrganizationCardModule } from './organization-card/organization-card.module';
import { UserButtonModule } from './user-button/user-button.module';
import { MemberCardModule } from './member-card/member-card.module';

@NgModule({
  imports: [CommonModule],
  exports: [
    AvatarModule,
    ButtonGithubModule,
    ChallengeCardModule,
    FooterModule,
    MemberCardModule,
    NavbarModule,
    OrganizationCardModule,
    UserButtonModule,
  ],
})
export class UiModule {}
