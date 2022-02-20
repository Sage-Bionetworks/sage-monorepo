import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { ButtonGithubComponent } from './button-github.component';

@NgModule({
  declarations: [ButtonGithubComponent],
  imports: [CommonModule, MatButtonModule],
  exports: [ButtonGithubComponent],
})
export class ButtonGithubModule {}
