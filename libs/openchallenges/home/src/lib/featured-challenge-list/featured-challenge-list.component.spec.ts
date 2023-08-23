import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';

import { FeaturedChallengeListComponent } from './featured-challenge-list.component';

describe('FeaturedChallengeListComponent', () => {
  let component: FeaturedChallengeListComponent;
  let fixture: ComponentFixture<FeaturedChallengeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, FeaturedChallengeListComponent],
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
