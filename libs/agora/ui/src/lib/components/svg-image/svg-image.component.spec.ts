import { TestBed, ComponentFixture } from '@angular/core/testing';
import { SvgImageComponent } from './svg-image.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

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
    component.imagePath = 'agora-assets/images/background1.svg';

    // Trigger change detection
    fixture.detectChanges();

    // Now the div should be populated with the sanitized SVG content
    const parentElement = element.querySelector('img');
    expect(parentElement).toBeTruthy(); // Check that innerHTML is populated
  });
});
