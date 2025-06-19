import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingIconComponent } from './loading-icon.component';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/models';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';

describe('LoadingIcon', () => {
  let component: LoadingIconComponent;
  let fixture: ComponentFixture<LoadingIconComponent>;

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
    fixture = TestBed.createComponent(LoadingIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
