import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingContainerComponent } from './loading-container.component';

describe('LoadingIcon', () => {
  let component: LoadingContainerComponent;
  let fixture: ComponentFixture<LoadingContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [provideLoadingIconColors()],
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
