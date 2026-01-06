import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewEncapsulation, computed, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ComparisonToolService, PlatformService } from '@sagebionetworks/explorers/services';
import { CookieService } from 'ngx-cookie-service';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DialogModule } from 'primeng/dialog';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-visualization-overview-panel',
  imports: [CommonModule, ButtonModule, CheckboxModule, DialogModule, FormsModule, TooltipModule],
  templateUrl: './visualization-overview-panel.component.html',
  styleUrls: ['./visualization-overview-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class VisualizationOverviewPanelComponent implements OnInit {
  comparisonToolService = inject(ComparisonToolService);
  platformService = inject(PlatformService);
  cookieService = inject(CookieService);
  viewConfig = this.comparisonToolService.viewConfig;

  readonly cookieName = 'hide_visualization_overview';

  /** Reads directly from cookie - the cookie is the source of truth */
  get willHide(): boolean {
    return this.platformService.isBrowser && this.cookieService.get(this.cookieName) === '1';
  }

  ngOnInit() {
    if (this.platformService.isBrowser && this.willHide) {
      this.comparisonToolService.setVisualizationOverviewVisibility(false);
    }
  }

  /** Saves to cookie immediately when checkbox changes */
  setWillHide(value: boolean) {
    if (value) {
      this.cookieService.set(this.cookieName, '1', { path: '/' });
    } else {
      this.cookieService.delete(this.cookieName, '/');
    }
  }

  activePane = 0;

  panes = computed(() => this.viewConfig().visualizationOverviewPanes);

  previous() {
    if (this.activePane > 0) {
      this.activePane--;
    }
  }

  next() {
    if (this.activePane < this.panes().length - 1) {
      this.activePane++;
    }
  }

  onHide() {
    this.activePane = 0;
  }

  toggle() {
    this.comparisonToolService.toggleVisualizationOverview();
  }

  close() {
    this.comparisonToolService.setVisualizationOverviewVisibility(false);
  }
}
