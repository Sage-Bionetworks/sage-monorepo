import { provideHttpClient } from '@angular/common/http';
import { DataversionService } from '@sagebionetworks/agora/api-client-angular';
import { ConfigService } from '@sagebionetworks/agora/config';
import { GitHubService } from '@sagebionetworks/agora/services';
import { configMock, dataversionMock } from '@sagebionetworks/agora/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import { of } from 'rxjs';
import { FooterComponent } from './footer.component';

const dataversionServiceMock = {
  getDataversion: jest.fn(() => of(dataversionMock)),
};

const shaMock = 'mock-sha';
const gitHubServiceMock = {
  getCommitSHA: jest.fn(() => of(shaMock)),
};

const configServiceMock = {
  config: configMock,
};

async function setup() {
  await render(FooterComponent, {
    providers: [
      provideHttpClient(),
      { provide: DataversionService, useValue: dataversionServiceMock },
      { provide: GitHubService, useValue: gitHubServiceMock },
      { provide: ConfigService, useValue: configServiceMock },
    ],
  });

  await waitFor(() => {
    expect(dataversionServiceMock.getDataversion).toHaveBeenCalledTimes(1);
    expect(gitHubServiceMock.getCommitSHA).toHaveBeenCalledTimes(1);
  });
}

describe('FooterComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should display site version', async () => {
    await setup();
    const siteversion = await screen.findByText('Site Version', { exact: false });
    expect(siteversion.textContent).toEqual(`Site Version ${configMock.appVersion}-${shaMock}`);
  });

  it('should return only version when SHA is empty', async () => {
    // Override the mock to return empty SHA
    gitHubServiceMock.getCommitSHA.mockReturnValue(of(''));

    const component = await render(FooterComponent, {
      providers: [
        provideHttpClient(),
        { provide: DataversionService, useValue: dataversionServiceMock },
        { provide: GitHubService, useValue: gitHubServiceMock },
        { provide: ConfigService, useValue: configServiceMock },
      ],
    });

    expect(component.fixture.componentInstance.getSiteVersion()).toBe(configMock.appVersion);
  });

  it('should display data version', async () => {
    await setup();
    const dataversion = await screen.findByText('Data Version', { exact: false });
    expect(dataversion.textContent).toEqual(
      `Data Version ${dataversionMock.data_file}-v${dataversionMock.data_version}`,
    );
  });
});
