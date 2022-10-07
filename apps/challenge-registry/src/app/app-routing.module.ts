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
    data: {
      seo: {
        title: 'About - Challenge Registry',
        metaTags: [
          {
            name: 'description',
            property: 'og:description',
            content: 'A description',
          },
          {
            name: 'title',
            property: 'og:title',
            content: 'About - Challenge Registry',
          },
          {
            name: 'image',
            property: 'og:image',
            content: 'https://avatars3.githubusercontent.com/u/16628445',
          },
          { property: 'og:url', content: 'https://challenge-registry.io' },
          {
            name: 'author',
            property: 'article:author',
            content: 'Marina Sirota, Tomiko Oskotsky, Alennie Roldan, ...',
          },
          // {
          //   name: 'publish_date',
          //   property: 'article:published_time',
          //   content: '2022-08-11T18:23:57+00:00',
          // },
          {
            name: 'keywords',
            content:
              'challenge, crowdsourcing, ai, microbiome, dream, sagebionetworks',
          },
          {
            name: 'twitter:card',
            content: 'summary_large_image',
          },
          {
            name: 'twitter:title',
            content: 'About - Challenge Registry',
          },
          {
            name: 'twitter:description',
            content: 'A description',
          },
          {
            name: 'twitter:url',
            content: 'https://challenge-registry.io',
          },
          {
            name: 'twitter:image:src',
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
