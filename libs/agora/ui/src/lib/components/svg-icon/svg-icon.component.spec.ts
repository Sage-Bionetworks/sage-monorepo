import { TestBed, ComponentFixture } from '@angular/core/testing';
import { SvgIconComponent } from './svg-icon.component';

describe('Component: SVG Icon', () => {
  let fixture: ComponentFixture<SvgIconComponent>;
  let component: SvgIconComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(SvgIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have SVG', () => {
    component.name = 'info-circle';
    fixture.detectChanges();
    expect(element.querySelector('svg')).toBeTruthy();
  });
});
