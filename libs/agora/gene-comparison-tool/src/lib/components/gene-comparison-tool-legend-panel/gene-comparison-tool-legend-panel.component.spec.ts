import { TestBed, ComponentFixture } from '@angular/core/testing';
import { GeneComparisonToolLegendPanelComponent } from './gene-comparison-tool-legend-panel.component';
import { provideRouter } from '@angular/router';

describe('Component: Gene Comparison Tool - Legend Panel', () => {
  let fixture: ComponentFixture<GeneComparisonToolLegendPanelComponent>;
  let component: GeneComparisonToolLegendPanelComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneComparisonToolLegendPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
