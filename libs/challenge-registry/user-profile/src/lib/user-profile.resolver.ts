import { Injectable } from '@angular/core';
import { makeStateKey, TransferState } from '@angular/platform-browser';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import { Observable, of } from 'rxjs';
import { AWESOME_USER_PROFILE, EMPTY_USER_PROFILE } from './mock-user-profiles';
import { UserProfile } from './user-profile';

@Injectable({
  providedIn: 'root',
})
export class UserProfileResolver implements Resolve<UserProfile> {
  constructor(
    private transferState: TransferState,
    private configService: ConfigService // @Inject(PLATFORM_ID) private platformId
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<UserProfile> {
    const username = route.params['login'];
    const userProfileKey = makeStateKey<UserProfile>(
      'userProfileKey-' + username
    );

    if (this.transferState.hasKey(userProfileKey)) {
      const userProfile = this.transferState.get(
        userProfileKey,
        EMPTY_USER_PROFILE
      );
      this.transferState.remove(userProfileKey);
      return of(userProfile);
    } else {
      // return this.coursesService.findCourseById(courseId).pipe(
      //   first(),
      //   tap((course) => {
      //     if (isPlatformServer(this.platformId)) {
      //       this.transferState.set(COURSE_KEY, course);
      //     }
      //   })
      // );
      return of(AWESOME_USER_PROFILE);
    }
  }
}
