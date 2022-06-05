import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonGithubModule } from './button-github/button-github.module';
import { FooterModule } from './footer/footer.module';
import { NavbarModule } from './navbar/navbar.module';
import { AvatarModule } from './avatar/avatar.module';
import { OrgCardModule } from './org-card/org-card.module';
import { UserButtonModule } from './user-button/user-button.module';

@NgModule({
  imports: [CommonModule],
  declarations: [],
  exports: [
    AvatarModule,
    ButtonGithubModule,
    FooterModule,
    NavbarModule,
    OrgCardModule,
    UserButtonModule,
  ],
})
export class ChallengeRegistryUiModule {}
