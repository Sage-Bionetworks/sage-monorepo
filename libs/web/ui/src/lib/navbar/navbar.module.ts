import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { NavbarComponent } from './navbar.component';
import { ButtonGithubModule } from '../button-github/button-github.module';

@NgModule({
  declarations: [NavbarComponent],
  imports: [CommonModule, MatButtonModule, ButtonGithubModule],
  exports: [NavbarComponent],
})
export class NavbarModule {}
