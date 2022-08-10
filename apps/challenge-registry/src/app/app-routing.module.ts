import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  // {
  //   path: 'home',
  //   loadChildren: () => import('./home/home.module').then((m) => m.HomeModule),
  // },
  {
    path: 'home',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/home').then(
        (m) => m.HomeModule
      ),
  },
  {
    path: 'about',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/about').then(
        (m) => m.AboutModule
      ),
  },
  {
    path: 'challenges',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/challenge-search').then(
        (m) => m.ChallengeSearchModule
      ),
  },
  {
    path: 'orgs',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/org-search').then(
        (m) => m.OrgSearchModule
      ),
  },
  {
    path: 'login',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/login').then(
        (m) => m.LoginModule
      ),
  },
  {
    path: 'signup',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/signup').then(
        (m) => m.SignupModule
      ),
  },
  {
    path: 'org/:login/:challenge',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/challenge').then(
        (m) => m.ChallengeModule
      ),
  },
  {
    path: 'org/:login',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/org-profile').then(
        (m) => m.OrgProfileModule
      ),
  },
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/not-found').then(
        (m) => m.NotFoundModule
      ),
  },
  {
    path: ':login',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/user-profile').then(
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
