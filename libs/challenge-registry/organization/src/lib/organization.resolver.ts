import { Injectable } from '@angular/core';
import { makeStateKey, TransferState } from '@angular/platform-browser';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';
import { Organization } from '@sagebionetworks/api-client-angular-deprecated';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import { Observable, of } from 'rxjs';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';
@Injectable({
  providedIn: 'root',
})
export class OrganizationResolver implements Resolve<Organization> {
  constructor(
    private transferState: TransferState,
    private configService: ConfigService // @Inject(PLATFORM_ID) private platformId
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Organization> {
    const username = route.params['login'];
    const organizationKey = makeStateKey<Organization>(
      'organizationKey-' + username
    );

    if (this.transferState.hasKey(organizationKey)) {
      const org = this.transferState.get(
        organizationKey,
        MOCK_ORGANIZATIONS[0]
      );
      this.transferState.remove(organizationKey);
      return of(org);
    } else {
      // return this.coursesService.findCourseById(courseId).pipe(
      //   first(),
      //   tap((course) => {
      //     if (isPlatformServer(this.platformId)) {
      //       this.transferState.set(COURSE_KEY, course);
      //     }
      //   })
      // );
      return of(MOCK_ORGANIZATIONS[0]);
    }
  }
}
