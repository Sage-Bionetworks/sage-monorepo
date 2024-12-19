import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SvgIconComponent } from './svg-icon.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('SvgIconComponent', () => {
  let fixture: ComponentFixture<SvgIconComponent>;
  let component: SvgIconComponent;
  let element: HTMLElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SvgIconComponent, NoopAnimationsModule],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(SvgIconComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate innerHTML in the div when imagePath changes', () => {
    // Mock input change to trigger ngOnChanges
    component.imagePath = '/agora-assets/icons/sample.svg';

    // Trigger change detection
    fixture.detectChanges();

    // Now the div should be populated with the sanitized SVG content
    const parentElement = element.querySelector('img');
    expect(parentElement).toBeTruthy(); // Check that innerHTML is populated
  });

  describe('getClasses', () => {
    it('should return only the default class when customClasses is not set', () => {
      component.customClasses = '';
      expect(component.getClasses()).toBe('svg-icon');
    });

    it('should append customClasses when it is set', () => {
      component.customClasses = 'custom-class';
      expect(component.getClasses()).toBe('svg-icon custom-class');
    });

    it('should handle multiple custom classes', () => {
      component.customClasses = 'custom-class another-class';
      expect(component.getClasses()).toBe('svg-icon custom-class another-class');
    });
  });
});
