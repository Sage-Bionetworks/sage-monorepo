import { registerInstrumentations } from '@opentelemetry/instrumentation';
import { WebTracerProvider, BatchSpanProcessor } from '@opentelemetry/sdk-trace-web';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http';
import { resourceFromAttributes } from '@opentelemetry/resources';
import { B3Propagator } from '@opentelemetry/propagator-b3';
import { ATTR_SERVICE_NAME, ATTR_SERVICE_VERSION } from '@opentelemetry/semantic-conventions';
import { DocumentLoadInstrumentation } from '@opentelemetry/instrumentation-document-load';
import { W3CTraceContextPropagator } from '@opentelemetry/core/build/src/trace/W3CTraceContextPropagator';
import { CompositePropagator } from '@opentelemetry/core';
import { UserInteractionInstrumentation } from '@opentelemetry/instrumentation-user-interaction';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { ZoneContextManager } from '@opentelemetry/context-zone-peer-dep';

export const telemetryFactory = (configService: ConfigService) => {
  return () => {
    console.log(`Telemetry enabled:`, configService.config.app.telemetry.enabled);

    if (!configService.config.app.telemetry.enabled) {
      return Promise.resolve();
    }
    if (configService.config.isPlatformServer) {
      console.log('Telemetry disabled on server-side rendering');
      return Promise.resolve();
    }

    const resource = resourceFromAttributes({
      [ATTR_SERVICE_NAME]: 'openchallenges-app',
      [ATTR_SERVICE_VERSION]: '1.0.0',
    });

    const otelCollectorOptions = {
      url: 'http://localhost:8500/otel-collector/v1/traces',
      headers: {}, // an optional object containing custom headers to be sent with each request
      concurrencyLimit: 10, // an optional limit on pending requests
    };

    const provider = new WebTracerProvider({
      resource,
      spanProcessors: [
        // new SimpleSpanProcessor(new ConsoleSpanExporter()),
        new BatchSpanProcessor(new OTLPTraceExporter(otelCollectorOptions), {
          // The maximum queue size. After the size is reached spans are dropped.
          maxQueueSize: 100,
          // The maximum batch size of every export. It must be smaller or equal to maxQueueSize.
          maxExportBatchSize: 10,
          // The interval between two consecutive exports
          scheduledDelayMillis: 500,
          // How long the export can run before it is cancelled
          exportTimeoutMillis: 30000,
        }),
      ],
    });

    provider.register({
      contextManager: new ZoneContextManager(),
      propagator: new CompositePropagator({
        propagators: [new B3Propagator(), new W3CTraceContextPropagator()],
      }),
    });

    // Registering instrumentations / plugins
    registerInstrumentations({
      instrumentations: [new DocumentLoadInstrumentation(), new UserInteractionInstrumentation()],
    });

    return Promise.resolve();
  };
};
