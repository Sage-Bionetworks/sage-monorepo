import { Injectable } from '@angular/core';
import {
  CanActivate,
  Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AuthService } from '../auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router, private authService: AuthService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | boolean {
    return this.authService.isLoggedIn().pipe(
      map((signedIn) => {
        if (!signedIn) {
          this.authService.setRedirectUrl(state.url);
          this.router.navigate([this.authService.getLoginUrl()]);
        }
        return signedIn;
      }),
      catchError((err) => {
        console.error(err);
        return of(false);
      })
    );
  }
}
