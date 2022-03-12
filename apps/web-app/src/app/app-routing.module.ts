import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'not-found',
    loadChildren: () =>
      import('@challenge-registry/web/pages').then((m) => m.NotFoundModule),
  },
  {
    path: '',
    pathMatch: 'full',
    loadChildren: () =>
      import('@challenge-registry/web/pages').then((m) => m.HomepageModule),
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
