import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { HelperService } from '@sagebionetworks/agora/services';
import { gctDetailsPanelDataMock } from '@sagebionetworks/agora/testing';
import { GeneComparisonToolDetailsPanelComponent } from './gene-comparison-tool-details-panel.component';

describe('Component: Gene Comparison Tool - Details Panel', () => {
  let fixture: ComponentFixture<GeneComparisonToolDetailsPanelComponent>;
  let component: GeneComparisonToolDetailsPanelComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [provideRouter([]), HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneComparisonToolDetailsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
    component.show({}, JSON.parse(JSON.stringify(gctDetailsPanelDataMock)));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have data', () => {
    expect(component.data).toEqual(gctDetailsPanelDataMock);
  });

  it('should have label', () => {
    const label = element.querySelector('.gct-details-panel-label') as HTMLElement;

    expect(label).toBeTruthy();
    expect(label?.innerHTML.trim()).toEqual(gctDetailsPanelDataMock.label as string);
  });

  it('should have heading', () => {
    const heading = element.querySelector('.gct-details-panel-heading') as HTMLElement;

    expect(heading).toBeTruthy();
    expect(heading?.innerHTML.trim()).toEqual(gctDetailsPanelDataMock.heading as string);
  });

  it('should have sub heading', () => {
    const subHeading = element.querySelector('.gct-details-panel-sub-heading') as HTMLElement;

    expect(subHeading).toBeTruthy();
    expect(subHeading?.innerHTML.trim()).toEqual(gctDetailsPanelDataMock.subHeading as string);
  });

  it('should have links', () => {
    expect(element.querySelector('.gct-details-panel-links')).toBeTruthy();
  });

  it('should have values', () => {
    const elements = fixture.debugElement.nativeElement.querySelectorAll(
      '.gct-details-panel-data > div > div',
    );
    expect(elements?.length).toEqual(4);

    expect(elements[0]?.innerHTML.trim()).toEqual(gctDetailsPanelDataMock.valueLabel?.toString());
    expect(elements[2]?.innerHTML.trim()).toEqual(gctDetailsPanelDataMock.value?.toString());
    expect(elements[3]?.innerHTML.trim()).toEqual(gctDetailsPanelDataMock.pValue?.toString());
  });
});
