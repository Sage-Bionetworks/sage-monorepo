import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WikiComponent } from './wiki.component';
import { provideHttpClient } from '@angular/common/http';

describe('WikiComponent', () => {
  let component: WikiComponent;
  let fixture: ComponentFixture<WikiComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [WikiComponent, provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(WikiComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('getClassName', () => {
    it('should default to the @Input className', () => {
      const expectedValue = 'test';

      component.className = expectedValue;
      const result = component.getClassName();

      expect(result.find((e) => e === expectedValue)).toBeTruthy();
    });

    it('should have a "loading" className when loading variable is true', () => {
      const expectedValue = 'loading';

      // set loading variable to be true
      component.loading = true;

      const result = component.getClassName();

      expect(result.find((e) => e === expectedValue)).toBeTruthy();
    });
  });
});
