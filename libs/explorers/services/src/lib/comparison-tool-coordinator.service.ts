import { Injectable, signal } from '@angular/core';
import { ComparisonToolService } from './comparison-tool.service';

/**
 * Coordinator to track which comparison tool service is currently active.
 * This prevents multiple CT services from simultaneously syncing to the URL.
 */
@Injectable({
  providedIn: 'root',
})
export class ComparisonToolCoordinatorService {
  private readonly activeServiceSignal = signal<ComparisonToolService<unknown> | null>(null);

  readonly activeService = this.activeServiceSignal.asReadonly();

  setActive(service: ComparisonToolService<unknown> | null): void {
    this.activeServiceSignal.set(service);
  }

  isActive(service: ComparisonToolService<unknown>): boolean {
    return this.activeService() === service;
  }
}
