import { TestBed, ComponentFixture } from '@angular/core/testing';
import { SvgImageComponent } from './svg-image.component';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SimpleChanges } from '@angular/core';

describe('SvgImageComponent', () => {
  let fixture: ComponentFixture<SvgImageComponent>;
  let component: SvgImageComponent;
  let element: HTMLElement;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SvgImageComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(SvgImageComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Check that there are no outstanding requests
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate innerHTML in the div when imagePath changes', () => {
    // Mock input change to trigger ngOnChanges
    component.imagePath = 'assets/images/sample.svg';

    // Simulate ngOnChanges call
    const changes: SimpleChanges = {
      imagePath: {
        currentValue: component.imagePath,
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true,
      },
    };
    component.ngOnChanges(changes); // Manually trigger ngOnChanges

    // Mock the HTTP request for the SVG file
    const req = httpMock.expectOne('assets/images/sample.svg');
    const mockSvgContent = `<svg
        version="1.1"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        viewBox="0 0 20 20"
        xml:space="preserve"
      >
        <circle class="svg-icon-bg" fill="transparent" cx="10" cy="10" r="9.5" />
        <path
          d="M9,15h2V9H9V15z M10,0C4.5,0,0,4.5,0,10s4.5,10,10,10s10-4.5,10-10S15.5,0,10,0z M10,18c-4.4,0-8-3.6-8-8s3.6-8,8-8 s8,3.6,8,8S14.4,18,10,18z M9,7h2V5H9V7z"
          fill="currentColor"
        />
      </svg>`;
    req.flush(mockSvgContent); // Simulate a successful HTTP response

    // Trigger change detection
    fixture.detectChanges();

    // Now the div should be populated with the sanitized SVG content
    const divElement = element.querySelector('div');
    expect(divElement?.innerHTML).toBeTruthy(); // Check that innerHTML is populated
    expect(divElement?.innerHTML).toContain('svg'); // Check that it contains the SVG content
  });
});
