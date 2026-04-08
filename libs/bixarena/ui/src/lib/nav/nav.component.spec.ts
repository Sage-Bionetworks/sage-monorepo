import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService } from '@sagebionetworks/bixarena/services';
import { NavComponent } from './nav.component';

describe('NavComponent', () => {
  let component: NavComponent;
  let fixture: ComponentFixture<NavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavComponent],
      providers: [
        provideRouter([]),
        {
          provide: ConfigService,
          useValue: {
            config: {},
          },
        },
        {
          provide: AuthService,
          useValue: {
            user: () => null,
            isAuthenticated: () => false,
            cachedUsername: () => null,
            login: () => undefined,
            logout: () => undefined,
            init: () => Promise.resolve(),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
