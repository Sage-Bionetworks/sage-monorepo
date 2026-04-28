import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [HomeComponent] }).compileComponents();
    fixture = TestBed.createComponent(HomeComponent);
    fixture.detectChanges();
  });

  it('renders the hero header and four section components in order', () => {
    const root = fixture.nativeElement as HTMLElement;
    const children = Array.from(root.querySelectorAll('.page-content > *')).map((el) =>
      el.tagName.toLowerCase(),
    );
    expect(children).toEqual([
      'header',
      'bixarena-composer-section',
      'bixarena-stats-section',
      'bixarena-trending-section',
      'bixarena-leaderboard-section',
    ]);
    expect(root.querySelector('.hero')).toBeTruthy();
  });
});
