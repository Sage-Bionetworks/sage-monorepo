import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth.service';
import { TokenModule } from './token/token.module';
import { GuardModule } from './guard/guard.module';
import { UserModule } from './user/user.module';

@NgModule({
  imports: [CommonModule],
  providers: [AuthService],
  exports: [GuardModule, TokenModule, UserModule],
})
export class WebAuthModule {}
