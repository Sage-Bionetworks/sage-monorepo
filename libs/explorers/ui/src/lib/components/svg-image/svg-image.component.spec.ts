import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SvgImageComponent } from './svg-image.component';

describe('SvgImageComponent', () => {
  let fixture: ComponentFixture<SvgImageComponent>;
  let component: SvgImageComponent;
  let element: HTMLElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SvgImageComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(SvgImageComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate innerHTML in the div when imagePath changes', () => {
    // Mock input change to trigger ngOnChanges
    component.imagePath = 'explorers-assets/images/background1.svg';

    // Trigger change detection
    fixture.detectChanges();

    // Now the div should be populated with the sanitized SVG content
    const parentElement = element.querySelector('img');
    expect(parentElement).toBeTruthy(); // Check that innerHTML is populated
  });

  it('should apply the correct class based on sizeMode', () => {
    component.imagePath = 'explorers-assets/images/background1.svg';

    component.sizeMode = 'full-height';
    fixture.detectChanges();
    expect(element).toHaveClass('svg-image-container--full-height');

    component.sizeMode = 'full-width';
    fixture.detectChanges();
    expect(element).toHaveClass('svg-image-container--full-width');

    component.sizeMode = 'original';
    fixture.detectChanges();
    expect(element).toHaveClass('svg-image-container--original');
  });
});
