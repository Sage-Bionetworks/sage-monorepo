import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { NgxTypedJsModule } from 'ngx-typed-js';
import { ChallengeSearchComponent } from './challenge-search.component';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';

@NgModule({
  declarations: [ChallengeSearchComponent],
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    NgxTypedJsModule,
    InputTextModule,
  ],
  exports: [ChallengeSearchComponent],
})
export class ChallengeSearchModule {}
