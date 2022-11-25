import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// import { AuthGuard } from './auth.guard';
// import { KAuthGuard } from '@sagebionetworks/challenge-registry/auth';
export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
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
    // <<<<<<< HEAD
    //     canActivate: [KAuthGuard],
    // =======
    data: {},
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
    path: 'team',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/team').then(
        (m) => m.TeamModule
      ),
  },
  // {
  //   path: 'org/:login/:challenge',
  //   loadChildren: () =>
  //     import('@sagebionetworks/challenge-registry/challenge').then(
  //       (m) => m.ChallengeModule
  //     ),
  // },
  // {
  //   path: 'org/:login',
  //   loadChildren: () =>
  //     import('@sagebionetworks/challenge-registry/org-profile').then(
  //       (m) => m.OrgProfileModule
  //     ),
  // },
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/not-found').then(
        (m) => m.NotFoundModule
      ),
  },
  {
    path: 'org',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/org-profile').then(
        (m) => m.OrgProfileModule
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
    RouterModule.forRoot(routes, {
      initialNavigation: 'enabledBlocking',
      // this is important to use "data:title" from any level
      // paramsInheritanceStrategy: 'always',
    }),
  ],
  declarations: [],
  providers: [],
  exports: [RouterModule],
})
export class AppRoutingModule {}
