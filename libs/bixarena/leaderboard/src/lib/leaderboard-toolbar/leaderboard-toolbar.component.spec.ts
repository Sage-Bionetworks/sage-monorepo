import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LeaderboardToolbarComponent } from './leaderboard-toolbar.component';

describe('LeaderboardToolbarComponent', () => {
  let component: LeaderboardToolbarComponent;
  let fixture: ComponentFixture<LeaderboardToolbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LeaderboardToolbarComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LeaderboardToolbarComponent);
    fixture.componentRef.setInput('categories', [
      { id: 'overall', name: 'Overall' },
      { id: 'cancer-biology', name: 'Cancer Biology' },
    ]);
    fixture.componentRef.setInput('activeCategoryId', 'overall');
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should mark the active category tab', () => {
    const buttons = (fixture.nativeElement as HTMLElement).querySelectorAll('.category-tab');
    expect(buttons).toHaveLength(2);
    expect(buttons[0].classList.contains('active')).toBe(true);
    expect(buttons[1].classList.contains('active')).toBe(false);
  });

  it('should emit categoryChange on tab click', () => {
    const emitted: string[] = [];
    component.categoryChange.subscribe((id: string) => emitted.push(id));
    const buttons = (fixture.nativeElement as HTMLElement).querySelectorAll('.category-tab');
    (buttons[1] as HTMLButtonElement).click();
    expect(emitted).toEqual(['cancer-biology']);
  });

  it('should emit filtersChange with updated license when a license pill is clicked', () => {
    const emitted: { license: string }[] = [];
    component.filtersChange.subscribe((value) => emitted.push(value));
    const pill = (fixture.nativeElement as HTMLElement).querySelectorAll(
      '.pill',
    )[1] as HTMLButtonElement;
    pill.click();
    expect(emitted).toEqual([{ license: 'open-source' }]);
  });
});
