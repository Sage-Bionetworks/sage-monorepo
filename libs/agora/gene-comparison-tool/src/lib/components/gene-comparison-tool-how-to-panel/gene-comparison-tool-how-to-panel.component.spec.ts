import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { GeneComparisonToolHowToPanelComponent } from './gene-comparison-tool-how-to-panel.component';

describe('Component: Gene Comparison Tool - How To Panel', () => {
  let fixture: ComponentFixture<GeneComparisonToolHowToPanelComponent>;
  let component: GeneComparisonToolHowToPanelComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneComparisonToolHowToPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
