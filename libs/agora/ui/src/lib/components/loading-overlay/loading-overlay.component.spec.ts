// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { LoadingOverlayComponent } from './loading-overlay.component';
import { HelperService } from '@sagebionetworks/agora/services';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Loading Overlay', () => {
  let component: LoadingOverlayComponent;
  let fixture: ComponentFixture<LoadingOverlayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [HelperService, provideRouter([])],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(LoadingOverlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
