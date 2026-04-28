import { afterNextRender, Component, signal } from '@angular/core';
import * as Sentry from '@sentry/angular';

interface SentryStatus {
  isInitialized: boolean;
  clientPresent: boolean;
  dsnMasked: string;
  environment: string;
  release: string;
  integrations: string;
  hasGlobal: boolean;
}

@Component({
  selector: 'app-sentry-test',
  template: `
    <section style="padding: 2rem; max-width: 900px; margin: 0 auto; font-family: sans-serif;">
      <h1>Sentry Test Page</h1>
      <p>
        This page is for diagnosing the Sentry integration. The status below reflects what the
        browser-loaded Sentry SDK reports about its own configuration.
      </p>

      <h2>Initialization Status</h2>
      <button type="button" (click)="refreshStatus()" style="margin-bottom: 0.75rem;">
        Refresh status
      </button>
      @if (status(); as s) {
        <table style="border-collapse: collapse; width: 100%;">
          <tbody>
            <tr>
              <th style="text-align: left; padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                Sentry.isInitialized()
              </th>
              <td style="padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                <strong [style.color]="s.isInitialized ? 'green' : 'red'">
                  {{ s.isInitialized }}
                </strong>
              </td>
            </tr>
            <tr>
              <th style="text-align: left; padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                Sentry client present
              </th>
              <td style="padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                {{ s.clientPresent }}
              </td>
            </tr>
            <tr>
              <th style="text-align: left; padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                DSN (masked)
              </th>
              <td style="padding: 0.25rem 0.75rem; border: 1px solid #ddd; font-family: monospace;">
                {{ s.dsnMasked }}
              </td>
            </tr>
            <tr>
              <th style="text-align: left; padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                Environment
              </th>
              <td style="padding: 0.25rem 0.75rem; border: 1px solid #ddd;">{{ s.environment }}</td>
            </tr>
            <tr>
              <th style="text-align: left; padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                Release
              </th>
              <td style="padding: 0.25rem 0.75rem; border: 1px solid #ddd;">{{ s.release }}</td>
            </tr>
            <tr>
              <th style="text-align: left; padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                Active integrations
              </th>
              <td style="padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                {{ s.integrations }}
              </td>
            </tr>
            <tr>
              <th style="text-align: left; padding: 0.25rem 0.75rem; border: 1px solid #ddd;">
                window.__SENTRY__ present
              </th>
              <td style="padding: 0.25rem 0.75rem; border: 1px solid #ddd;">{{ s.hasGlobal }}</td>
            </tr>
          </tbody>
        </table>

        @if (!s.isInitialized) {
          <p style="color: red; margin-top: 0.75rem;">
            Sentry is NOT initialized. Buttons below will likely no-op or throw uncaught errors.
            Check the browser console for any <code>[Sentry]</code> warnings emitted during init.
          </p>
        }
      } @else {
        <p>Status not yet computed (SSR pre-hydration).</p>
      }

      <h2 style="margin-top: 2rem;">Trigger Test Events</h2>
      <p>
        Each button uses a different code path. If Sentry is set up correctly, all should produce an
        event. Watch the Network tab for POSTs to <code>*.ingest.sentry.io/.../envelope/</code> with
        <code>"type":"event"</code> on line 2 of the request payload.
      </p>
      <div style="display: flex; flex-direction: column; gap: 0.5rem; max-width: 400px;">
        <button type="button" (click)="captureExceptionDirect()">
          1. Sentry.captureException (direct API call)
        </button>
        <button type="button" (click)="captureMessageDirect()">
          2. Sentry.captureMessage (direct API call)
        </button>
        <button type="button" (click)="throwInClickHandler()">
          3. Throw synchronously inside Angular click handler
        </button>
        <button type="button" (click)="throwInSetTimeout()">
          4. Throw inside setTimeout (window.onerror path)
        </button>
        <button type="button" (click)="rejectPromise()">
          5. Unhandled Promise rejection (unhandledrejection path)
        </button>
      </div>

      @if (lastEventId()) {
        <div style="margin-top: 1rem; padding: 0.75rem; background: #eef; border-radius: 4px;">
          <strong>Last event ID:</strong>
          <code>{{ lastEventId() }}</code>
          <p style="margin: 0.25rem 0 0;">
            Search this ID in Sentry to confirm delivery. Only buttons 1 and 2 return an ID
            synchronously; for the throw-based paths, look at the request payload in the Network
            tab.
          </p>
        </div>
      }

      @if (lastResultMessage()) {
        <p style="margin-top: 0.5rem;">{{ lastResultMessage() }}</p>
      }
    </section>
  `,
})
export class SentryTestComponent {
  readonly status = signal<SentryStatus | undefined>(undefined);
  readonly lastEventId = signal<string | undefined>(undefined);
  readonly lastResultMessage = signal<string | undefined>(undefined);

  constructor() {
    afterNextRender(() => this.refreshStatus());
  }

  refreshStatus(): void {
    this.status.set(this.computeStatus());
  }

  captureExceptionDirect(): void {
    const message = `captureException test ${Date.now()}`;
    const id = Sentry.captureException(new Error(message));
    this.lastEventId.set(id);
    this.lastResultMessage.set(`Called Sentry.captureException — returned event ID ${id}.`);
  }

  captureMessageDirect(): void {
    const message = `captureMessage test ${Date.now()}`;
    const id = Sentry.captureMessage(message);
    this.lastEventId.set(id);
    this.lastResultMessage.set(`Called Sentry.captureMessage — returned event ID ${id}.`);
  }

  throwInClickHandler(): void {
    this.lastResultMessage.set(
      'Threw inside Angular click handler — should hit Sentry.createErrorHandler.',
    );
    throw new Error(`sync click throw test ${Date.now()}`);
  }

  throwInSetTimeout(): void {
    this.lastResultMessage.set(
      'Scheduled setTimeout throw — should hit window.onerror via GlobalHandlers integration.',
    );
    setTimeout(() => {
      throw new Error(`setTimeout throw test ${Date.now()}`);
    }, 0);
  }

  rejectPromise(): void {
    this.lastResultMessage.set(
      'Created unhandled rejection — should hit unhandledrejection via GlobalHandlers integration.',
    );
    Promise.reject(new Error(`promise rejection test ${Date.now()}`));
  }

  private computeStatus(): SentryStatus {
    const client = Sentry.getClient();
    const options = client?.getOptions();
    const dsn = (options?.dsn as string | undefined) ?? '';
    const integrations = options?.integrations;

    let integrationNames = '(none)';
    if (Array.isArray(integrations) && integrations.length > 0) {
      integrationNames = integrations.map((i) => i.name).join(', ');
    } else if (typeof integrations === 'function') {
      integrationNames = '(lazy function — call after init to inspect)';
    }

    return {
      isInitialized: typeof Sentry.isInitialized === 'function' ? Sentry.isInitialized() : !!client,
      clientPresent: !!client,
      dsnMasked: dsn ? this.maskDsn(dsn) : '(empty)',
      environment: (options?.environment as string | undefined) ?? '(unset)',
      release: (options?.release as string | undefined) ?? '(unset)',
      integrations: integrationNames,
      hasGlobal:
        typeof window !== 'undefined' && Object.prototype.hasOwnProperty.call(window, '__SENTRY__'),
    };
  }

  private maskDsn(dsn: string): string {
    return dsn.replace(/(https:\/\/)([^@]{4})[^@]+(@)/, '$1$2***$3');
  }
}
