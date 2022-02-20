import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonGithubModule } from './button-github/button-github.module';
import { FooterModule } from './footer/footer.module';
import { NavbarModule } from './navbar/navbar.module';

@NgModule({
  imports: [CommonModule, ButtonGithubModule, FooterModule, NavbarModule],
  declarations: [],
  exports: [ButtonGithubModule, FooterModule, NavbarModule],
})
export class WebUiModule {}
