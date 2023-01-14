import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { UserProfileResolver } from './user-profile.resolver';

describe('UserProfileResolver', () => {
  let resolver: UserProfileResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientModule] });
    resolver = TestBed.inject(UserProfileResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
