import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  {
    path: 'about',
    loadChildren: () =>
      import('@sagebionetworks/web/about').then((m) => m.AboutModule),
  },
  {
    path: 'challenges',
    loadChildren: () =>
      import('@sagebionetworks/web/challenge-search').then(
        (m) => m.ChallengeSearchModule
      ),
  },
  {
    path: 'orgs',
    loadChildren: () =>
      import('@sagebionetworks/web/org-search').then((m) => m.OrgSearchModule),
  },
  {
    path: 'login',
    loadChildren: () =>
      import('@sagebionetworks/web/login').then((m) => m.LoginModule),
  },
  {
    path: 'signup',
    loadChildren: () =>
      import('@sagebionetworks/web/signup').then((m) => m.SignupModule),
  },
  {
    path: 'org/:login/:challenge',
    loadChildren: () =>
      import('@sagebionetworks/web/challenge').then((m) => m.ChallengeModule),
  },
  {
    path: 'org/:login',
    loadChildren: () =>
      import('@sagebionetworks/web/org-profile').then(
        (m) => m.OrgProfileModule
      ),
  },
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/web/not-found').then((m) => m.NotFoundModule),
  },
  {
    path: '',
    pathMatch: 'full',
    loadChildren: () =>
      import('@sagebionetworks/web/home').then((m) => m.HomeModule),
    // canActivate: [AuthGuard],
  },
  {
    path: ':login',
    loadChildren: () =>
      import('@sagebionetworks/web/user-profile').then(
        (m) => m.UserProfileModule
      ),
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { initialNavigation: 'enabledBlocking' }),
  ],
  declarations: [],
  providers: [],
  exports: [RouterModule],
})
export class AppRoutingModule {}
