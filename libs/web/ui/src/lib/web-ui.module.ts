import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { ButtonGithubComponent } from './button-github/button-github.component';
import { FooterComponent } from './footer/footer.component';

@NgModule({
  imports: [CommonModule, MatButtonModule],
  declarations: [ButtonGithubComponent, FooterComponent],
  exports: [ButtonGithubComponent, FooterComponent],
})
export class WebUiModule {}
