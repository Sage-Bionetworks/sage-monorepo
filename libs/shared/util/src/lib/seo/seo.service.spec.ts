import { TestBed } from '@angular/core/testing';

import { SeoService } from './seo.service';

describe('SeoServiceService', () => {
  let service: SeoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SeoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
