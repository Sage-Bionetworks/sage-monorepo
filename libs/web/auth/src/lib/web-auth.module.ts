import { ModuleWithProviders, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth.service';
import { TokenModule } from './token/token.module';
import { GuardModule } from './guard/guard.module';
import { UserModule } from './user/user.module';

@NgModule({
  imports: [CommonModule],
  exports: [GuardModule, TokenModule, UserModule],
})
export class WebAuthModule {
  static forRoot(): ModuleWithProviders<WebAuthModule> {
    return {
      ngModule: WebAuthModule,
      providers: [AuthService],
    };
  }
}
