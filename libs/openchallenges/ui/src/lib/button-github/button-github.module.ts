import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { ButtonGithubComponent } from './button-github.component';

@NgModule({
  declarations: [ButtonGithubComponent],
  imports: [CommonModule, MatButtonModule],
  exports: [ButtonGithubComponent],
})
export class ButtonGithubModule {}
