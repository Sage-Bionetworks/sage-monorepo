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

  it('should set license filter when toggled from default', () => {
    const emitted: { license: string | null }[] = [];
    component.filtersChange.subscribe((value) => emitted.push(value));
    component.toggleLicense('open-source');
    expect(emitted).toEqual([{ license: 'open-source' }]);
  });

  it('should clear license to null when the active license is toggled again', () => {
    fixture.componentRef.setInput('filters', { license: 'open-source' });
    fixture.detectChanges();
    const emitted: { license: string | null }[] = [];
    component.filtersChange.subscribe((value) => emitted.push(value));
    component.toggleLicense('open-source');
    expect(emitted).toEqual([{ license: null }]);
  });

  it('should report active filter count', () => {
    expect(component.activeFilterCount()).toBe(0);
    expect(component.hasActiveFilters()).toBe(false);
    fixture.componentRef.setInput('filters', { license: 'commercial' });
    fixture.detectChanges();
    expect(component.activeFilterCount()).toBe(1);
    expect(component.hasActiveFilters()).toBe(true);
  });

  it('should not render the active-filters rail when no filters are active', () => {
    expect((fixture.nativeElement as HTMLElement).querySelector('.active-filters')).toBeNull();
  });

  it('should render an active-filter chip when a license is set', () => {
    fixture.componentRef.setInput('filters', { license: 'open-source' });
    fixture.detectChanges();
    const chip = (fixture.nativeElement as HTMLElement).querySelector(
      '.active-filters .chip-value',
    );
    expect(chip?.textContent?.trim()).toBe('Open Source');
  });

  it('should reset a specific filter via resetFilter(key)', () => {
    fixture.componentRef.setInput('filters', { license: 'open-source' });
    fixture.detectChanges();
    const emitted: { license: string | null }[] = [];
    component.filtersChange.subscribe((value) => emitted.push(value));
    component.resetFilter('license');
    expect(emitted).toEqual([{ license: null }]);
  });

  it('should not emit when resetting a filter already at its default', () => {
    const emitted: unknown[] = [];
    component.filtersChange.subscribe((value) => emitted.push(value));
    component.resetFilter('license');
    expect(emitted).toEqual([]);
  });

  it('should emit a reset of all filters via clearAllFilters', () => {
    fixture.componentRef.setInput('filters', { license: 'commercial' });
    fixture.detectChanges();
    const emitted: { license: string | null }[] = [];
    component.filtersChange.subscribe((value) => emitted.push(value));
    component.clearAllFilters();
    expect(emitted).toEqual([{ license: null }]);
  });
});
