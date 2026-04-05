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
              app: {
                version: 'test',
                termsOfServiceUrl: 'https://example.com/tos',
                contactUrl: 'https://example.com/contact',
                feedbackUrl: 'https://example.com/feedback',
                sageBionetworksUrl: 'https://sagebionetworks.org',
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
