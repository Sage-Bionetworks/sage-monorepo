import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SynapseApiService } from '@sagebionetworks/agora/services';
import { synapseWikiMock } from '@sagebionetworks/agora/testing';
import { server } from '@sagebionetworks/agora/testing/msw';
import { http, HttpResponse } from 'msw';
import { WikiComponent } from './wiki.component';

describe('WikiComponent', () => {
  beforeAll(() => server.listen());
  afterEach(() => server.restoreHandlers());
  afterAll(() => server.close());

  let component: WikiComponent;
  let fixture: ComponentFixture<WikiComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [provideHttpClient(), SynapseApiService],
    }).compileComponents();

    fixture = TestBed.createComponent(WikiComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('getWikiData', () => {
    it('should get wiki data', async () => {
      const expected = synapseWikiMock;
      expect(component.loading).toBe(true);

      component.getWikiData();
      await fixture.whenStable();

      expect(component.data.markdown).toBe(expected.markdown);
      expect(component.loading).toBe(false);
    });

    it('should default to error content when api is unreachable', async () => {
      const noDataContent = `<div class="wiki-no-data">No data found...</div>`;
      expect(component.loading).toBe(true);

      // simulate server error
      const url = 'https://repo-prod.prod.sagebase.org/repo/v1/entity/syn25913473/wiki/';
      server.use(
        http.get(
          url,
          () => {
            return HttpResponse.error();
          },
          {
            once: true,
          },
        ),
      );

      component.getWikiData();
      await fixture.whenStable();

      expect(component.safeHtml).toBe(noDataContent);
      expect(component.loading).toBe(false);
    });
  });

  describe('getClassName', () => {
    it('should default to the @Input className', () => {
      const expectedValue = 'test';

      component.className = expectedValue;
      const result = component.getClassName();

      expect(result.find((e) => e === expectedValue)).toBeTruthy();
    });

    it('should have a "loading" className when loading variable is true', () => {
      const expectedValue = 'loading';

      // set loading variable to be true
      component.loading = true;

      const result = component.getClassName();

      expect(result.find((e) => e === expectedValue)).toBeTruthy();
    });
  });
});
