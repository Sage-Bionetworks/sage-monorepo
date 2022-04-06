import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChallengeStargazersComponent } from './challenge-stargazers.component';

describe('ChallengeStargazersComponent', () => {
  let component: ChallengeStargazersComponent;
  let fixture: ComponentFixture<ChallengeStargazersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChallengeStargazersComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChallengeStargazersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
