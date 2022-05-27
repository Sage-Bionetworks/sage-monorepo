import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  {
    path: 'about',
    loadChildren: () =>
      import('@sage-bionetworks/web/about').then((m) => m.AboutModule),
  },
  {
    path: 'challenges',
    loadChildren: () =>
      import('@sage-bionetworks/web/challenge-search').then(
        (m) => m.ChallengeSearchModule
      ),
  },
  {
    path: 'orgs',
    loadChildren: () =>
      import('@sage-bionetworks/web/org-search').then((m) => m.OrgSearchModule),
  },
  {
    path: 'login',
    loadChildren: () =>
      import('@sage-bionetworks/web/login').then((m) => m.LoginModule),
  },
  {
    path: 'signup',
    loadChildren: () =>
      import('@sage-bionetworks/web/signup').then((m) => m.SignupModule),
  },
  {
    path: 'org/:login/:challenge',
    loadChildren: () =>
      import('@sage-bionetworks/web/challenge').then((m) => m.ChallengeModule),
  },
  {
    path: 'org/:login',
    loadChildren: () =>
      import('@sage-bionetworks/web/org-profile').then(
        (m) => m.OrgProfileModule
      ),
  },
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sage-bionetworks/web/not-found').then((m) => m.NotFoundModule),
  },
  {
    path: '',
    pathMatch: 'full',
    loadChildren: () =>
      import('@sage-bionetworks/web/home').then((m) => m.HomeModule),
    // canActivate: [AuthGuard],
  },
  {
    path: ':login',
    loadChildren: () =>
      import('@sage-bionetworks/web/user-profile').then(
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
