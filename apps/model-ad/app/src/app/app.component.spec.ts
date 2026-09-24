import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import {
  MetaTagService,
  PlatformService,
  SvgIconService,
  VersionService,
} from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { DataVersionService } from '@sagebionetworks/model-ad/api-client';
import { ConfigService } from '@sagebionetworks/model-ad/config';
import { configMock, dataVersionMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { AppComponent } from './app.component';

const PAGE_TITLE = 'Model AD';

async function setup() {
  const metaTagService = { initialize: jest.fn() };
  await render(AppComponent, {
    providers: [
      provideRouter([]),
      provideHttpClient(),
      provideHttpClientTesting(),
      MessageService,
      { provide: ConfigService, useValue: { config: configMock } },
      { provide: PlatformService, useValue: { isServer: false, isBrowser: true } },
      { provide: DataVersionService, useValue: { getDataVersion: () => of(dataVersionMock) } },
      { provide: MetaTagService, useValue: metaTagService },
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });
  return { metaTagService };
}

describe('AppComponent', () => {
  it('should initialize page meta tags for Model AD', async () => {
    const { metaTagService } = await setup();
    expect(metaTagService.initialize).toHaveBeenCalledWith(PAGE_TITLE);
  });

  it('should show the data version in the footer', async () => {
    await setup();
    const formattedDataVersion = TestBed.inject(VersionService).formatDataVersion(dataVersionMock);
    expect(screen.getByText(`Data Version ${formattedDataVersion}`)).toBeVisible();
  });
});
