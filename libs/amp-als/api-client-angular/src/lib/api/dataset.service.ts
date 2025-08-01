/**
 * AMP-ALS API
 * Discover, explore, and contribute to AMP-ALS datasets.
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpResponse,
  HttpEvent,
  HttpParameterCodec,
  HttpContext,
} from '@angular/common/http';
import { CustomHttpParameterCodec } from '../encoder';
import { Observable } from 'rxjs';

// @ts-ignore
import { BasicError } from '../model/basicError';
// @ts-ignore
import { Dataset } from '../model/dataset';
// @ts-ignore
import { DatasetSearchQuery } from '../model/datasetSearchQuery';
// @ts-ignore
import { DatasetsPage } from '../model/datasetsPage';

// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS } from '../variables';
import { Configuration } from '../configuration';

@Injectable({
  providedIn: 'root',
})
export class DatasetService {
  protected basePath = 'http://localhost/v1';
  public defaultHeaders = new HttpHeaders();
  public configuration = new Configuration();
  public encoder: HttpParameterCodec;

  constructor(
    protected httpClient: HttpClient,
    @Optional() @Inject(BASE_PATH) basePath: string | string[],
    @Optional() configuration: Configuration,
  ) {
    if (configuration) {
      this.configuration = configuration;
    }
    if (typeof this.configuration.basePath !== 'string') {
      if (Array.isArray(basePath) && basePath.length > 0) {
        basePath = basePath[0];
      }

      if (typeof basePath !== 'string') {
        basePath = this.basePath;
      }
      this.configuration.basePath = basePath;
    }
    this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
  }

  // @ts-ignore
  private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
    if (typeof value === 'object' && value instanceof Date === false) {
      httpParams = this.addToHttpParamsRecursive(httpParams, value);
    } else {
      httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
    }
    return httpParams;
  }

  private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
    if (value == null) {
      return httpParams;
    }

    if (typeof value === 'object') {
      if (Array.isArray(value)) {
        (value as any[]).forEach(
          (elem) => (httpParams = this.addToHttpParamsRecursive(httpParams, elem, key)),
        );
      } else if (value instanceof Date) {
        if (key != null) {
          httpParams = httpParams.append(key, (value as Date).toISOString().substr(0, 10));
        } else {
          throw Error('key may not be null if value is Date');
        }
      } else {
        Object.keys(value).forEach(
          (k) =>
            (httpParams = this.addToHttpParamsRecursive(
              httpParams,
              value[k],
              key != null ? `${key}[${k}]` : k,
            )),
        );
      }
    } else if (key != null) {
      httpParams = httpParams.append(key, value);
    } else {
      throw Error('key may not be null if value is not object or array');
    }
    return httpParams;
  }

  /**
   * Get a dataset
   * Returns the dataset specified
   * @param datasetId The unique identifier of the dataset.
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getDataset(
    datasetId: number,
    observe?: 'body',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: 'application/json' | 'application/problem+json';
      context?: HttpContext;
    },
  ): Observable<Dataset>;
  public getDataset(
    datasetId: number,
    observe?: 'response',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: 'application/json' | 'application/problem+json';
      context?: HttpContext;
    },
  ): Observable<HttpResponse<Dataset>>;
  public getDataset(
    datasetId: number,
    observe?: 'events',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: 'application/json' | 'application/problem+json';
      context?: HttpContext;
    },
  ): Observable<HttpEvent<Dataset>>;
  public getDataset(
    datasetId: number,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: {
      httpHeaderAccept?: 'application/json' | 'application/problem+json';
      context?: HttpContext;
    },
  ): Observable<any> {
    if (datasetId === null || datasetId === undefined) {
      throw new Error(
        'Required parameter datasetId was null or undefined when calling getDataset.',
      );
    }

    let localVarHeaders = this.defaultHeaders;

    let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (localVarHttpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['application/json', 'application/problem+json'];
      localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (localVarHttpHeaderAcceptSelected !== undefined) {
      localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
    }

    let localVarHttpContext: HttpContext | undefined = options && options.context;
    if (localVarHttpContext === undefined) {
      localVarHttpContext = new HttpContext();
    }

    let responseType_: 'text' | 'json' | 'blob' = 'json';
    if (localVarHttpHeaderAcceptSelected) {
      if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
        responseType_ = 'text';
      } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
        responseType_ = 'json';
      } else {
        responseType_ = 'blob';
      }
    }

    let localVarPath = `/datasets/${this.configuration.encodeParam({ name: 'datasetId', value: datasetId, in: 'path', style: 'simple', explode: false, dataType: 'number', dataFormat: 'int64' })}`;
    return this.httpClient.get<Dataset>(`${this.configuration.basePath}${localVarPath}`, {
      context: localVarHttpContext,
      responseType: <any>responseType_,
      withCredentials: this.configuration.withCredentials,
      headers: localVarHeaders,
      observe: observe,
      reportProgress: reportProgress,
    });
  }

  /**
   * List datasets
   * List datasets
   * @param datasetSearchQuery The search query used to find datasets.
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public listDatasets(
    datasetSearchQuery?: DatasetSearchQuery,
    observe?: 'body',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: 'application/json' | 'application/problem+json';
      context?: HttpContext;
    },
  ): Observable<DatasetsPage>;
  public listDatasets(
    datasetSearchQuery?: DatasetSearchQuery,
    observe?: 'response',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: 'application/json' | 'application/problem+json';
      context?: HttpContext;
    },
  ): Observable<HttpResponse<DatasetsPage>>;
  public listDatasets(
    datasetSearchQuery?: DatasetSearchQuery,
    observe?: 'events',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: 'application/json' | 'application/problem+json';
      context?: HttpContext;
    },
  ): Observable<HttpEvent<DatasetsPage>>;
  public listDatasets(
    datasetSearchQuery?: DatasetSearchQuery,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: {
      httpHeaderAccept?: 'application/json' | 'application/problem+json';
      context?: HttpContext;
    },
  ): Observable<any> {
    let localVarQueryParameters = new HttpParams({ encoder: this.encoder });
    if (datasetSearchQuery !== undefined && datasetSearchQuery !== null) {
      localVarQueryParameters = this.addToHttpParams(
        localVarQueryParameters,
        <any>datasetSearchQuery,
        'datasetSearchQuery',
      );
    }

    let localVarHeaders = this.defaultHeaders;

    let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (localVarHttpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['application/json', 'application/problem+json'];
      localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (localVarHttpHeaderAcceptSelected !== undefined) {
      localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
    }

    let localVarHttpContext: HttpContext | undefined = options && options.context;
    if (localVarHttpContext === undefined) {
      localVarHttpContext = new HttpContext();
    }

    let responseType_: 'text' | 'json' | 'blob' = 'json';
    if (localVarHttpHeaderAcceptSelected) {
      if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
        responseType_ = 'text';
      } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
        responseType_ = 'json';
      } else {
        responseType_ = 'blob';
      }
    }

    let localVarPath = `/datasets`;
    return this.httpClient.get<DatasetsPage>(`${this.configuration.basePath}${localVarPath}`, {
      context: localVarHttpContext,
      params: localVarQueryParameters,
      responseType: <any>responseType_,
      withCredentials: this.configuration.withCredentials,
      headers: localVarHeaders,
      observe: observe,
      reportProgress: reportProgress,
    });
  }
}
