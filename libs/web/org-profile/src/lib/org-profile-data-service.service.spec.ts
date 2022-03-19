import { TestBed } from '@angular/core/testing';

import { OrgProfileDataServiceService } from './org-profile-data-service.service';

describe('OrgProfileDataServiceService', () => {
  let service: OrgProfileDataServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrgProfileDataServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
