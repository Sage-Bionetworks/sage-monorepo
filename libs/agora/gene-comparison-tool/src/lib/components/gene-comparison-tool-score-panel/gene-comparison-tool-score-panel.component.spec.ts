import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { HelperService } from '@sagebionetworks/agora/services';
import { gctScorePanelDataMock } from '@sagebionetworks/agora/testing';
import { GeneComparisonToolScorePanelComponent } from './gene-comparison-tool-score-panel.component';

describe('Component: Gene Comparison Tool - Score Panel', () => {
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
    // Add container element so that the popover can be appended to it and offsetHeight exists for popover.show
    const targetElement = document.createElement('div');
    targetElement.id = 'test-target';
    document.body.appendChild(targetElement);

    fixture = TestBed.createComponent(GeneComparisonToolScorePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;

    const mockEvent = new MouseEvent('click');
    Object.defineProperty(mockEvent, 'target', { value: targetElement });
    component.show(mockEvent, JSON.parse(JSON.stringify(gctScorePanelDataMock)));
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
