import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { provideRouter } from '@angular/router';
import { HelperService } from '@sagebionetworks/agora/services';
import { gctScorePanelDataMock } from '@sagebionetworks/agora/testing';
import { GeneComparisonToolScorePanelComponent } from './gene-comparison-tool-score-panel.component';

describe('Component: Gene Comparison Tool - Details Panel', () => {
  let fixture: ComponentFixture<GeneComparisonToolScorePanelComponent>;
  let component: GeneComparisonToolScorePanelComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [HelperService, provideRouter([])],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneComparisonToolScorePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
    component.show(new Event('click'), JSON.parse(JSON.stringify(gctScorePanelDataMock)));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have data', () => {
    expect(component.data).toEqual(gctScorePanelDataMock);
  });

  it('should have links', () => {
    expect(element.querySelector('.gct-score-panel-links')).toBeTruthy();
  });
});
