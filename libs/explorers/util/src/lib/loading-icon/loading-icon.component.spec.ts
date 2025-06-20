import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingIconComponent } from './loading-icon.component';
import { getModelAdTestProviders } from '@sagebionetworks/model-ad/testing';

describe('LoadingIcon', () => {
  let component: LoadingIconComponent;
  let fixture: ComponentFixture<LoadingIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [...getModelAdTestProviders()],
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
