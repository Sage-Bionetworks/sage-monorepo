import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchViewerComponent } from './search-viewer.component';

describe('SearchViewerComponent', () => {
  let component: SearchViewerComponent;
  let fixture: ComponentFixture<SearchViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SearchViewerComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
