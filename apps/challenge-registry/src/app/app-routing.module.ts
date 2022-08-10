import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  {
    path: 'about',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/about').then(
        (m) => m.AboutModule
      ),
    data: {
      seo: {
        title: 'About Page - Challenge Registry',
        metaTags: [
          {
            name: 'description',
            property: 'og:description',
            content: 'A description',
          },
          {
            name: 'title',
            property: 'og:title',
            content: 'About Page - Challenge Registry',
          },
          {
            name: 'image',
            property: 'og:image',
            content: 'https://avatars3.githubusercontent.com/u/16628445',
          },
          { property: 'og:url', content: 'https://challenge-registry.io' },
          {
            name: 'twitter:card',
            content: 'https://avatars3.githubusercontent.com/u/16628445',
          },
        ],
      },
    },
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
    path: '',
    pathMatch: 'full',
    loadChildren: () =>
      import('@sagebionetworks/challenge-registry/home').then(
        (m) => m.HomeModule
      ),
    // canActivate: [AuthGuard],
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
