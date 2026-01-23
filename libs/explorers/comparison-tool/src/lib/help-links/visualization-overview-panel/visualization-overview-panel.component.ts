import { CommonModule } from '@angular/common';
import { AfterViewChecked, Component, ElementRef, ViewEncapsulation, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
  EXPLORERS_APP_CONFIG,
  PlatformService,
} from '@sagebionetworks/explorers/services';
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
export class VisualizationOverviewPanelComponent implements AfterViewChecked {
  comparisonToolService = inject(ComparisonToolService);
  platformService = inject(PlatformService);
  private elementRef = inject(ElementRef);
  private appConfig = inject(EXPLORERS_APP_CONFIG);

  get willHide(): boolean {
    return this.comparisonToolService.isVisualizationOverviewHiddenByUser();
  }

  setWillHide(value: boolean) {
    this.comparisonToolService.setVisualizationOverviewHiddenByUser(value);
  }

  activePane = 0;
  private lastPlayedPane = -1;

  /** Gets the panes to display in the visualization overview dialog from app config. */
  get panes(): VisualizationOverviewPane[] {
    return this.appConfig.visualizationOverviewPanes;
  }

  get activePaneHasVideo(): boolean {
    if (this.panes.length === 0) return false;
    return this.panes[this.activePane]?.content?.includes('<video') ?? false;
  }

  get dialogStyle(): Record<string, string> {
    return this.activePaneHasVideo
      ? { width: '95vw', maxWidth: '1200px' }
      : { width: '100%', maxWidth: '580px' };
  }

  ngAfterViewChecked() {
    if (
      this.platformService.isBrowser &&
      this.comparisonToolService.isVisualizationOverviewVisible() &&
      this.activePane !== this.lastPlayedPane
    ) {
      this.initializeMediaInActivePane();
      this.lastPlayedPane = this.activePane;
    }
  }

  private initializeMediaInActivePane() {
    const activePane = this.elementRef.nativeElement.querySelector(
      '.visualization-overview-panel-pane.active',
    );
    if (!activePane) return;

    // Handle videos
    const videos = activePane.querySelectorAll('video');
    videos.forEach((video: HTMLVideoElement) => {
      video.classList.add('loading');

      const onCanPlay = () => {
        video.classList.remove('loading');
        video.removeEventListener('canplay', onCanPlay);
      };
      video.addEventListener('canplay', onCanPlay);

      const playPromise = video.play();
      if (playPromise !== undefined) {
        playPromise.catch(() => {
          video.classList.remove('loading');
        });
      }
    });

    // Handle images
    const images = activePane.querySelectorAll('.image-container img');
    images.forEach((img: HTMLImageElement) => {
      if (img.complete) {
        img.classList.remove('loading');
      } else {
        img.classList.add('loading');
        const onLoad = () => {
          img.classList.remove('loading');
          img.removeEventListener('load', onLoad);
        };
        img.addEventListener('load', onLoad);
      }
    });
  }

  previous() {
    if (this.activePane > 0) {
      this.activePane--;
    }
  }

  next() {
    if (this.activePane < this.panes.length - 1) {
      this.activePane++;
    }
  }

  onHide() {
    this.activePane = 0;
    this.lastPlayedPane = -1;
  }

  close() {
    this.comparisonToolService.setVisualizationOverviewVisibility(false);
  }
}
