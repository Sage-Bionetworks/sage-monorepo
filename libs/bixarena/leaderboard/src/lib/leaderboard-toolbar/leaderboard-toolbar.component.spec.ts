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
      { id: 'neuroscience', name: 'Neuroscience' },
    ]);
    fixture.componentRef.setInput('activeCategoryId', 'overall');
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the active category name in the trigger', () => {
    const trigger = (fixture.nativeElement as HTMLElement).querySelector(
      '.category-trigger .value',
    );
    expect(trigger?.textContent?.trim()).toBe('Overall');
  });

  it('should narrow filtered categories when search is set', () => {
    component.pickerSearch.set('neuro');
    expect(component.filteredCategories().map((c) => c.id)).toEqual(['neuroscience']);
  });

  it('should emit categoryChange when a picker option is selected', () => {
    const emitted: string[] = [];
    component.categoryChange.subscribe((id: string) => emitted.push(id));
    component.onCategorySelect('cancer-biology');
    expect(emitted).toEqual(['cancer-biology']);
  });

  it('should reset picker search after selection', () => {
    component.pickerSearch.set('cancer');
    component.onCategorySelect('cancer-biology');
    expect(component.pickerSearch()).toBe('');
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
