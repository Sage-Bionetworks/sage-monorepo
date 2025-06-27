import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingContainerComponent } from './loading-container.component';
import { LOADING_ICON_COLORS_PROVIDER } from '@sagebionetworks/model-ad/testing';

describe('LoadingIcon', () => {
  let component: LoadingContainerComponent;
  let fixture: ComponentFixture<LoadingContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [LOADING_ICON_COLORS_PROVIDER],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(LoadingContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
