import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChallengeOverviewComponent } from './challenge-overview.component';

describe('ChallengeOverviewComponent', () => {
  let component: ChallengeOverviewComponent;
  let fixture: ComponentFixture<ChallengeOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChallengeOverviewComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChallengeOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
