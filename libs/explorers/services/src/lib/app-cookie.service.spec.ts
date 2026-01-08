import { TestBed } from '@angular/core/testing';
import { CookieService } from 'ngx-cookie-service';
import { AppCookieService } from './app-cookie.service';
import { PlatformService } from './platform.service';

describe('AppCookieService', () => {
  const createCookieServiceMock = () => ({
    get: jest.fn(),
    set: jest.fn(),
    delete: jest.fn(),
  });

  const createPlatformServiceMock = (isBrowser: boolean, isServer: boolean) => ({
    isBrowser,
    isServer,
  });

  function setup({
    cookieValue = '',
    isServer = false,
  }: {
    cookieValue?: string;
    isServer?: boolean;
  } = {}) {
    const cookieServiceMock = createCookieServiceMock();
    const platformServiceMock = createPlatformServiceMock(!isServer, isServer);

    cookieServiceMock.get.mockReturnValue(cookieValue);

    TestBed.configureTestingModule({
      providers: [
        { provide: CookieService, useValue: cookieServiceMock },
        { provide: PlatformService, useValue: platformServiceMock },
      ],
    });

    const service = TestBed.inject(AppCookieService);

    return { service, cookieServiceMock, platformServiceMock };
  }

  it('returns false on server', () => {
    const { service, cookieServiceMock } = setup({ isServer: true, cookieValue: '1' });
    expect(service.isVisualizationOverviewHidden()).toBe(false);
    expect(cookieServiceMock.get).not.toHaveBeenCalled();
  });

  it('returns false when cookie not set', () => {
    const { service, cookieServiceMock } = setup({ cookieValue: '' });
    expect(service.isVisualizationOverviewHidden()).toBe(false);
    expect(cookieServiceMock.get).toHaveBeenCalledWith('hide_visualization_overview');
  });

  it('returns true when cookie value is "1"', () => {
    const { service } = setup({ cookieValue: '1' });
    expect(service.isVisualizationOverviewHidden()).toBe(true);
  });

  it('writes cookie when hiding', () => {
    const { service, cookieServiceMock } = setup();
    service.setVisualizationOverviewHidden(true);
    expect(cookieServiceMock.set).toHaveBeenCalledWith('hide_visualization_overview', '1', {
      path: '/',
    });
  });

  it('deletes cookie when showing', () => {
    const { service, cookieServiceMock } = setup();
    service.setVisualizationOverviewHidden(false);
    expect(cookieServiceMock.delete).toHaveBeenCalledWith('hide_visualization_overview', '/');
  });

  it('does not write cookie on server', () => {
    const { service, cookieServiceMock } = setup({ isServer: true });
    service.setVisualizationOverviewHidden(true);
    expect(cookieServiceMock.set).not.toHaveBeenCalled();
    expect(cookieServiceMock.delete).not.toHaveBeenCalled();
  });
});
