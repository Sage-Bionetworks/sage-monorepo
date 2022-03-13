import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { NgxTypedJsModule } from 'ngx-typed-js';
import { ChallengeSearchComponent } from './challenge-search.component';

@NgModule({
  declarations: [ChallengeSearchComponent],
  imports: [CommonModule, MatButtonModule, NgxTypedJsModule],
  exports: [ChallengeSearchComponent],
})
export class ChallengeSearchModule {}
