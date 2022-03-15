import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonGithubModule } from './button-github/button-github.module';
import { FooterModule } from './footer/footer.module';
import { NavbarModule } from './navbar/navbar.module';
import { AvatarModule } from './avatar/avatar.module';

@NgModule({
  imports: [CommonModule],
  declarations: [],
  exports: [AvatarModule, ButtonGithubModule, FooterModule, NavbarModule],
})
export class WebUiModule {}
