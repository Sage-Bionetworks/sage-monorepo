import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchDropdownFilterComponent } from './search-dropdown-filter.component';

describe('SearchDropdownFilterComponent', () => {
  let component: SearchDropdownFilterComponent;
  let fixture: ComponentFixture<SearchDropdownFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SearchDropdownFilterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SearchDropdownFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
