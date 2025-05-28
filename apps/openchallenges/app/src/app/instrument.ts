import opentelemetry from '@opentelemetry/api';
import { registerInstrumentations } from '@opentelemetry/instrumentation';
import {
  WebTracerProvider,
  ConsoleSpanExporter,
  SimpleSpanProcessor,
  BatchSpanProcessor,
} from '@opentelemetry/sdk-trace-web';
import { getWebAutoInstrumentations } from '@opentelemetry/auto-instrumentations-web';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http';
import { resourceFromAttributes } from '@opentelemetry/resources';
import { B3Propagator } from '@opentelemetry/propagator-b3';
import { ATTR_SERVICE_NAME, ATTR_SERVICE_VERSION } from '@opentelemetry/semantic-conventions';
import { OTLPMetricExporter } from '@opentelemetry/exporter-metrics-otlp-http';
import { MeterProvider, PeriodicExportingMetricReader } from '@opentelemetry/sdk-metrics';

const resource = resourceFromAttributes({
  [ATTR_SERVICE_NAME]: 'openchallenges-app',
  [ATTR_SERVICE_VERSION]: '1.0.0',
});

// const provider = new WebTracerProvider({ resource });

// provider.addSpanProcessor(new SimpleSpanProcessor(new ConsoleSpanExporter()));

// provider.addSpanProcessor(
//   new BatchSpanProcessor(
//     new OTLPTraceExporter({
//       url: 'https://ingest.<REGION>.signoz.cloud:443/v1/traces',
//       headers: {
//         'signoz-ingestion-key': '<SIGNOZ_INGESTION_KEY>',
//       },
//     }),
//   ),
// );

// provider.register({
//   propagator: new B3Propagator(),
// });

// registerInstrumentations({
//   instrumentations: [
//     getWebAutoInstrumentations({
//       '@opentelemetry/instrumentation-document-load': {},
//       '@opentelemetry/instrumentation-user-interaction': {},
//       '@opentelemetry/instrumentation-fetch': {
//         propagateTraceHeaderCorsUrls: /.+/,
//       },
//       '@opentelemetry/instrumentation-xml-http-request': {
//         propagateTraceHeaderCorsUrls: /.+/,
//       },
//     }),
//   ],
// });

// const metricReader = new PeriodicExportingMetricReader({
//   exporter: new OTLPMetricExporter({
//     url: 'https://ingest.<REGION>.signoz.cloud:443/v1/metrics',
//     headers: {
//       'signoz-ingestion-key': '<SIGNOZ_INGESTION_KEY>',
//     },
//   }),
//   // Default is 60000ms (60 seconds). Set to 10 seconds for demonstrative purposes only.
//   exportIntervalMillis: 6000,
// });

// const myServiceMeterProvider = new MeterProvider({
//   resource,
//   readers: [metricReader],
// });

// // Set this MeterProvider to be global to the app being instrumented.
// opentelemetry.metrics.setGlobalMeterProvider(myServiceMeterProvider);
