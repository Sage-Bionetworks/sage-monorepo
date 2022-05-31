import { Injectable } from '@angular/core';
import { Organization } from '@sagebionetworks/api-angular';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OrgProfileDataServiceService {
  private org: BehaviorSubject<Organization | undefined> = new BehaviorSubject<
    Organization | undefined
  >(undefined);

  setOrg(org: Organization | undefined): void {
    this.org.next(org);
  }

  getOrg(): Observable<Organization | undefined> {
    return this.org.asObservable();
    // .pipe(filter((challenge) => challenge !== undefined));
  }
}
