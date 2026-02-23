import { CommonModule } from '@angular/common';
import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  HostListener,
  inject,
  input,
  output,
} from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faAngleLeft, faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { Panel } from '@sagebionetworks/explorers/models';
import { HelperService, PlatformService } from '@sagebionetworks/explorers/services';

@Component({
  selector: 'explorers-panel-navigation',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './panel-navigation.component.html',
  styleUrls: ['./panel-navigation.component.scss'],
})
export class PanelNavigationComponent implements AfterViewInit, AfterViewChecked {
  helperService = inject(HelperService);
  private readonly platformService = inject(PlatformService);

  panels = input.required<Panel[]>();
  activePanel = input.required<string>();
  activeParent = input<string>('');
  scrollToPanelNavElementOnInitialLoad = input<boolean>(false);
  panelChange = output<Panel>();

  faAngleRight = faAngleRight;
  faAngleLeft = faAngleLeft;

  private readonly DEFAULT_NAV_SLIDE_INDEX = 0;
  navSlideIndex = this.DEFAULT_NAV_SLIDE_INDEX;

  @HostListener('window:scroll')
  onWindowScroll() {
    if (this.platformService.isBrowser) {
      const nav = document.querySelector<HTMLElement>('.panel-navigation');
      const rect = nav?.getBoundingClientRect();

      if (rect && rect.y <= 0) {
        nav?.classList.add('sticky');
      } else {
        nav?.classList.remove('sticky');
      }
    }
  }

  @HostListener('window:resize')
  onWindowResize() {
    if (this.platformService.isBrowser) {
      const nav = document.querySelector<HTMLElement>('.panel-navigation');
      const navContainer = nav?.querySelector<HTMLElement>('.panel-navigation-container');
      const navList = nav?.querySelector<HTMLElement>('.panel-navigation-container > ul');
      const navItems = nav?.querySelectorAll<HTMLElement>('.panel-navigation-container > ul > li');
      let navItemsWidth = 0;
      if (navItems) {
        for (const element of Array.from(navItems)) {
          navItemsWidth += element.offsetWidth;
        }
      }

      if (navContainer && navList && navItemsWidth) {
        if (navItemsWidth > navContainer.offsetWidth) {
          nav?.classList.add('scrollable');
        } else {
          nav?.classList.remove('scrollable');
          this.navSlideIndex = 0;
          navList.style.marginLeft = '0px';
        }
      }
    }
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.onWindowResize();
    }, 100);

    if (this.scrollToPanelNavElementOnInitialLoad()) {
      this.scrollToPanelNavElement();
    }
  }

  ngAfterViewChecked() {
    this.onWindowResize();
  }

  activatePanel(panel: Panel) {
    if (this.platformService.isBrowser) {
      this.panelChange.emit(panel);
      this.scrollToPanelNavElement();
    }
  }

  scrollToPanelNavElement() {
    // Add slight delay to allow panel to render before scrolling
    setTimeout(() => {
      const nav = document.querySelector('.panel-navigation');
      if (nav) {
        window.scrollTo({
          top: this.helperService.getOffset(nav).top,
          left: 0,
          behavior: 'smooth',
        });
      }
    }, 100);
  }

  getPanelCount() {
    return this.panels().filter((p: Panel) => !p.disabled).length;
  }

  onNavigationItemClick(panel: Panel) {
    this.activatePanel(panel);
  }

  slideNavigation(direction: number) {
    this.navSlideIndex += direction;

    if (this.navSlideIndex < 0) {
      this.navSlideIndex = 0;
    } else if (this.navSlideIndex > this.getPanelCount() - 1) {
      this.navSlideIndex = this.getPanelCount() - 1;
    }

    const nav = document.querySelector<HTMLElement>('.panel-navigation');
    const navList = nav?.querySelector<HTMLElement>('.panel-navigation-container > ul');
    const navItems = nav?.querySelectorAll<HTMLElement>('.panel-navigation-container > ul > li');

    if (navList && navItems) {
      let navItemsWidth = 0;

      for (let i = 0; i < this.navSlideIndex; ++i) {
        navItemsWidth += navItems[i].offsetWidth;
      }

      if (this.navSlideIndex > 0) {
        navItemsWidth += 20;
      }

      navList.style.marginLeft = navItemsWidth * -1 + 'px';
    }
  }

  isPanelActive(panel: Panel): boolean {
    return this.activePanel() === panel.name || this.activeParent() === panel.name;
  }
}
