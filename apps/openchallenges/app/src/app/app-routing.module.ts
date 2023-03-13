import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// import { AuthGuard } from './auth.guard';
// import { KAuthGuard } from '@sagebionetworks/openchallenges/auth';
export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  {
    path: 'home',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/home').then((m) => m.HomeModule),
  },
  {
    path: 'about',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/about').then(
        (routes) => routes.routes
      ),
    data: {},
  },
  {
    path: 'challenge',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/challenge-search').then(
        (m) => m.ChallengeSearchModule
      ),
  },
  {
    path: 'organization',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/org-search').then(
        (m) => m.OrgSearchModule
      ),
  },
  {
    path: 'login',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/login').then(
        (m) => m.LoginModule
      ),
  },
  {
    path: 'signup',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/signup').then(
        (m) => m.SignupModule
      ),
  },
  {
    path: 'team',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/team').then((m) => m.TeamModule),
  },
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/not-found').then(
        (m) => m.NotFoundModule
      ),
  },
  {
    path: 'organization/:organizationId',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/org-profile').then(
        (m) => m.OrgProfileModule
      ),
  },
  {
    path: 'challenge/:challengeId/:challengeSlug',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/challenge').then(
        (m) => m.ChallengeModule
      ),
  },
  {
    path: 'user',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/user-profile').then(
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
