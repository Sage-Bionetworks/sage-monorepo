import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { LoadingIconComponent } from './loading-icon.component';

describe('LoadingIcon', () => {
  let component: LoadingIconComponent;
  let fixture: ComponentFixture<LoadingIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [provideLoadingIconColors()],
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
