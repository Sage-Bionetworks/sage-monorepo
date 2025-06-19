import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingContainerComponent } from './loading-container.component';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/models';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';

describe('LoadingIcon', () => {
  let component: LoadingContainerComponent;
  let fixture: ComponentFixture<LoadingContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [
        {
          provide: LOADING_ICON_COLORS,
          useValue: MODEL_AD_LOADING_ICON_COLORS,
        },
      ],
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
