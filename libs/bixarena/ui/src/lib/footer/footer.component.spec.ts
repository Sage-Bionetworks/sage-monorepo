import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { FooterComponent } from './footer.component';

describe('FooterComponent', () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterComponent],
      providers: [
        {
          provide: ConfigService,
          useValue: {
            config: {
              app: { version: 'test' },
              links: {
                termsOfService: 'https://example.com/tos',
                contact: 'https://example.com/contact',
                feedback: 'https://example.com/feedback',
                sageBionetworks: 'https://sagebionetworks.org',
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
