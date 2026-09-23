import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MetaTagService, PlatformService } from '@sagebionetworks/explorers/services';
import { DataVersionService } from '@sagebionetworks/model-ad/api-client';
import { ConfigService } from '@sagebionetworks/model-ad/config';
import { configMock, dataVersionMock } from '@sagebionetworks/model-ad/testing';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { AppComponent } from './app.component';

const PAGE_TITLE = 'Model AD';

describe('AppComponent', () => {
  let metaTagService: { initialize: jest.Mock };

  beforeEach(async () => {
    metaTagService = { initialize: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        MessageService,
        { provide: ConfigService, useValue: { config: configMock } },
        { provide: PlatformService, useValue: { isServer: false, isBrowser: true } },
        { provide: DataVersionService, useValue: { getDataVersion: () => of(dataVersionMock) } },
        { provide: MetaTagService, useValue: metaTagService },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should initialize page meta tags for Model AD', () => {
    TestBed.createComponent(AppComponent);
    expect(metaTagService.initialize).toHaveBeenCalledWith(PAGE_TITLE);
  });
});
