import { TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService } from './auth.service';

const mockUser = {
  sub: 'abc-123',
  preferred_username: 'testuser',
  email: 'test@example.com',
  email_verified: true,
  roles: [],
  avatar_url: 'https://example.com/avatar.png',
};

const mockConfig = {
  config: {
    auth: { baseUrls: { csr: 'http://127.0.0.1:8000' } },
  },
};

describe('AuthService', () => {
  let service: AuthService;
  const originalFetch = globalThis.fetch;

  beforeEach(() => {
    localStorage.clear();
    globalThis.fetch = jest.fn();

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: ConfigService, useValue: mockConfig },
      ],
    });
    service = TestBed.inject(AuthService);
  });

  afterEach(() => {
    globalThis.fetch = originalFetch;
  });

  const mockFetch = () => globalThis.fetch as jest.Mock;

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  describe('init()', () => {
    it('should set user on successful /userinfo', async () => {
      mockFetch().mockResolvedValue({ ok: true, json: () => Promise.resolve(mockUser) });
      await service.init();
      expect(service.user()).toEqual(mockUser);
      expect(service.isAuthenticated()).toBe(true);
    });

    it('should cache user in localStorage', async () => {
      mockFetch().mockResolvedValue({ ok: true, json: () => Promise.resolve(mockUser) });
      await service.init();
      const cached = JSON.parse(localStorage.getItem('ba-user') ?? '{}');
      expect(cached.username).toBe('testuser');
      expect(cached.avatarUrl).toBe('https://example.com/avatar.png');
    });

    it('should clear cache on non-ok response', async () => {
      localStorage.setItem('ba-user', JSON.stringify({ username: 'old' }));
      mockFetch().mockResolvedValue({ ok: false });
      await service.init();
      expect(service.user()).toBeNull();
      expect(localStorage.getItem('ba-user')).toBeNull();
    });

    it('should clear cache on fetch error', async () => {
      localStorage.setItem('ba-user', JSON.stringify({ username: 'old' }));
      mockFetch().mockRejectedValue(new Error('network error'));
      await service.init();
      expect(service.user()).toBeNull();
      expect(localStorage.getItem('ba-user')).toBeNull();
    });
  });

  describe('logout()', () => {
    beforeEach(async () => {
      mockFetch().mockResolvedValue({ ok: true, json: () => Promise.resolve(mockUser) });
      await service.init();
      mockFetch().mockClear();
    });

    it('should clear state on successful logout', async () => {
      mockFetch().mockResolvedValue({ ok: true });
      await service.logout();
      expect(service.user()).toBeNull();
      expect(localStorage.getItem('ba-user')).toBeNull();
    });

    it('should not clear state on failed logout', async () => {
      mockFetch().mockResolvedValue({ ok: false, status: 500 });
      await service.logout();
      expect(service.user()).toEqual(mockUser);
      expect(service.isAuthenticated()).toBe(true);
    });

    it('should not clear state on network error', async () => {
      mockFetch().mockRejectedValue(new Error('network error'));
      await service.logout();
      expect(service.user()).toEqual(mockUser);
      expect(service.isAuthenticated()).toBe(true);
    });
  });

  describe('cachedUser', () => {
    it('should load cached user from localStorage', () => {
      localStorage.setItem(
        'ba-user',
        JSON.stringify({ username: 'cached', avatarUrl: 'https://example.com/img.png' }),
      );

      TestBed.resetTestingModule();
      TestBed.configureTestingModule({
        providers: [
          AuthService,
          { provide: PLATFORM_ID, useValue: 'browser' },
          { provide: ConfigService, useValue: mockConfig },
        ],
      });
      const freshService = TestBed.inject(AuthService);
      expect(freshService.cachedUser()).toEqual({
        username: 'cached',
        avatarUrl: 'https://example.com/img.png',
      });
    });

    it('should return null when no cache exists', () => {
      expect(service.cachedUser()).toBeNull();
    });
  });
});
