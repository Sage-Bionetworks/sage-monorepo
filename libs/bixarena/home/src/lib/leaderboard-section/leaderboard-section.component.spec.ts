import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LeaderboardSectionComponent } from './leaderboard-section.component';

describe('LeaderboardSectionComponent', () => {
  let fixture: ComponentFixture<LeaderboardSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LeaderboardSectionComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(LeaderboardSectionComponent);
    fixture.detectChanges();
  });

  it('renders the section', () => {
    expect(fixture.nativeElement.querySelector('.leaderboard-section')).toBeTruthy();
  });
});
