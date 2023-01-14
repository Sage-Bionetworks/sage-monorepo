import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeaturedChallengeListComponent } from './featured-challenge-list.component';

describe('FeaturedChallengeListComponent', () => {
  let component: FeaturedChallengeListComponent;
  let fixture: ComponentFixture<FeaturedChallengeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FeaturedChallengeListComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeaturedChallengeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
