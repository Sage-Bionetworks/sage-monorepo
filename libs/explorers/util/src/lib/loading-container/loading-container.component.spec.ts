import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingContainerComponent } from './loading-container.component';
import { getModelAdTestProviders } from '@sagebionetworks/model-ad/testing';

describe('LoadingIcon', () => {
  let component: LoadingContainerComponent;
  let fixture: ComponentFixture<LoadingContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [...getModelAdTestProviders()],
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
