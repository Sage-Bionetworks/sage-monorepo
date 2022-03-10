import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
// import { AvatarModule, FooterModule } from '@sage-bionetworks/sage-angular';
import { HomepageComponent } from './homepage.component';
// import { MaterialModule } from '@shared/material/material.module';
// import { NgxTypedJsModule } from 'ngx-typed-js-public';
// import { CountUpModule } from 'ngx-countup';
import { WebFeatureHomeModule } from '@challenge-registry/web/feature-home';

const routes: Routes = [{ path: '', component: HomepageComponent }];

@NgModule({
  declarations: [HomepageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    WebFeatureHomeModule,
    // FooterModule,
    // MaterialModule,
    // AvatarModule,
    // NgxTypedJsModule,
    // CountUpModule,
  ],
  exports: [HomepageComponent],
})
export class HomepageModule {}
