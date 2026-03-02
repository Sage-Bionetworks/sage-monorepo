import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { HelperService } from '@sagebionetworks/agora/services';
import { gctFiltersMocks } from '@sagebionetworks/agora/testing';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { GeneComparisonToolFilterPanelComponent } from './gene-comparison-tool-filter-panel.component';

describe('Component: Gene Comparison Tool - Filter Panel', () => {
  let fixture: ComponentFixture<GeneComparisonToolFilterPanelComponent>;
  let component: GeneComparisonToolFilterPanelComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [
        provideRouter([]),
        HelperService,
        provideHttpClient(),
        { provide: SvgIconService, useClass: SvgIconServiceStub },
      ],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneComparisonToolFilterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
    component.filters = JSON.parse(JSON.stringify(gctFiltersMocks));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have data', () => {
    expect(component.filters).toEqual(gctFiltersMocks);
  });

  it('should display filters', () => {
    expect(element.querySelectorAll('.gct-filter-panel-pane').length).toEqual(
      gctFiltersMocks.length,
    );
    expect(element.querySelectorAll('.gct-filter-panel-pane:first-child li').length).toEqual(
      gctFiltersMocks[0].options.length,
    );
  });

  it('should have close button', () => {
    expect(element.querySelector('.gct-filter-panel-close')).toBeTruthy();
  });

  it('should open', () => {
    // Set to close
    component.isOpen = false;

    // Open programmatically
    component.open();
    expect(component.isOpen).toEqual(true);

    fixture.detectChanges();

    // Make sure panel is open
    const panel = element.querySelector('.gct-filter-panel');
    expect(panel?.classList?.contains('open')).toEqual(true);
  });

  it('should close', () => {
    component.isOpen = true;
    component.open();

    // Close programmatically
    component.close();
    expect(component.isOpen).toEqual(false);

    // Set to open
    component.isOpen = true;

    fixture.detectChanges();

    // Close with click event
    const closeButton = element.querySelector('.gct-filter-panel-close') as HTMLElement;
    closeButton.click();
    expect(component.isOpen).toEqual(false);

    fixture.detectChanges();

    // Make sure panel is close
    const panel = element.querySelector('.gct-filter-panel');
    expect(panel?.classList?.contains('open')).toEqual(false);
  });

  it('should toggle', () => {
    // Set to open
    component.isOpen = true;

    // Toggle (close) programmatically
    component.toggle();
    expect(component.isOpen).toEqual(false);

    fixture.detectChanges();

    // Make sure panel is close
    const panel = element.querySelector('.gct-filter-panel');
    expect(panel?.classList?.contains('open')).toEqual(false);
  });

  it('should open pane', () => {
    // Set to all close
    component.activePane = -1;

    // Open first pane programmatically
    component.openPane(0);
    expect(component.activePane).toEqual(0);

    fixture.detectChanges();

    // Make sure first pane is open
    const pane = element.querySelector('.gct-filter-panel-pane:first-child');
    expect(pane?.classList?.contains('open')).toEqual(true);
  });

  it('should close pane', () => {
    /// Set to first pane
    component.activePane = 0;

    // Open first pane programmatically
    component.closePanes();
    expect(component.activePane).toEqual(-1);

    fixture.detectChanges();

    // Make sure first pane is open
    const pane = element.querySelector('.gct-filter-panel-pane:first-child');
    expect(pane?.classList?.contains('open')).toEqual(false);
  });

  it('should toggle option', () => {
    // Toggle (check) first option with click event
    const checkbox = element.querySelector(
      '.gct-filter-panel-pane:first-child li:first-child .ui-chkbox-box',
    ) as HTMLElement | null;
    checkbox?.click();

    fixture.detectChanges();

    // Make sure input reflects changes
    const input = element.querySelector(
      '.gct-filter-panel-pane:first-child li:first-child input',
    ) as HTMLInputElement;
    expect(input?.checked).toBe(true);
  });
});
