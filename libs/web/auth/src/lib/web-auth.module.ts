import { ModuleWithProviders, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth.service';
import { TokenModule } from './token/token.module';
import { GuardModule } from './guard/guard.module';
import { UserProfileModule } from './user/user.module';

@NgModule({
  imports: [CommonModule],
  exports: [GuardModule, TokenModule, UserProfileModule],
})
export class WebAuthModule {
  static forRoot(): ModuleWithProviders<WebAuthModule> {
    return {
      ngModule: WebAuthModule,
      providers: [AuthService],
    };
  }
}
