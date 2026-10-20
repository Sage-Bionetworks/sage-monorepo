import { Injectable } from '@angular/core';
import { ParamMap } from '@angular/router';
import { PANEL_ROUTE_PARAMS } from '@sagebionetworks/explorers/constants';
import { Panel, QueryParams } from '@sagebionetworks/explorers/models';

@Injectable({
  providedIn: 'root',
})
export class HelperService {
  getScrollTop() {
    if (typeof window === 'undefined' || typeof document == 'undefined') {
      return { x: 0, y: 0 };
    }

    const supportPageOffset = window.pageXOffset !== undefined;
    const isCSS1Compat = (document.compatMode || '') === 'CSS1Compat';

    if (supportPageOffset) {
      return {
        x: window.pageXOffset,
        y: window.pageYOffset,
      };
    } else if (isCSS1Compat) {
      return {
        x: document.documentElement.scrollLeft,
        y: document.documentElement.scrollTop,
      };
    } else {
      return {
        x: document.body.scrollLeft,
        y: document.body.scrollTop,
      };
    }
  }

  getOffset(el: any) {
    if (typeof window === 'undefined' || typeof document === 'undefined' || !el) {
      return { top: 0, left: 0 };
    }

    const rect = el.getBoundingClientRect();
    const scroll = this.getScrollTop();
    return {
      top: rect.top + scroll.y,
      left: rect.left + scroll.x,
    };
  }

  appendQueryParams(url: string, queryParams: QueryParams) {
    const queryString = this.toQueryString(
      Object.entries(queryParams).filter((entry): entry is [string, string] => !!entry[1]),
    );
    if (!queryString) return url;
    const fragmentIndex = url.includes('#') ? url.indexOf('#') : url.length;
    const pathAndQuery = url.slice(0, fragmentIndex);
    const fragment = url.slice(fragmentIndex);
    const separator = pathAndQuery.includes('?') ? '&' : '?';
    return `${pathAndQuery}${separator}${queryString}${fragment}`;
  }

  /**
   * Sets query parameters on a URL, for example before passing it to `Location.replaceState`.
   * A null, undefined, or empty value deletes its parameter. Other parameters keep their order,
   * and the hash fragment is kept.
   */
  setQueryParams(url: string, queryParams: QueryParams) {
    const fragmentIndex = url.includes('#') ? url.indexOf('#') : url.length;
    const pathAndQuery = url.slice(0, fragmentIndex);
    const fragment = url.slice(fragmentIndex);
    const queryIndex = pathAndQuery.includes('?') ? pathAndQuery.indexOf('?') : pathAndQuery.length;
    const path = pathAndQuery.slice(0, queryIndex);

    const params = new URLSearchParams(pathAndQuery.slice(queryIndex));
    for (const [key, value] of Object.entries(queryParams)) {
      if (value) {
        params.set(key, value);
      } else {
        params.delete(key);
      }
    }

    const queryString = this.toQueryString([...params]);
    const query = queryString ? '?' + queryString : '';
    return `${path}${query}${fragment}`;
  }

  private toQueryString(entries: [string, string][]) {
    return entries
      .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
      .join('&');
  }

  getPanelUrl(
    basePath: string,
    activePanel: string,
    activeParent: string,
    queryParams: QueryParams = {},
  ) {
    const panelPath = activeParent === '' ? activePanel : `${activeParent}/${activePanel}`;
    return this.appendQueryParams(`${basePath}/${panelPath}`, queryParams);
  }

  handlePanelChange(
    panels: Panel[],
    panel: Panel,
    basePath: string,
    queryParams: QueryParams = {},
  ): { activePanel: string; activeParent: string; url: string } | undefined {
    if (panel.disabled) return undefined;
    const { activePanel, activeParent } = this.getActivePanelAndParent(panels, panel);
    const url = this.getPanelUrl(basePath, activePanel, activeParent, queryParams);
    return { activePanel, activeParent, url };
  }

  getActivePanelAndParent(
    panels: Panel[],
    panel: Panel,
  ): { activePanel: string; activeParent: string } {
    if (panel.children) {
      return {
        activePanel: panel.children[0].name,
        activeParent: panel.name,
      };
    } else if (!panels.find((p: Panel) => p.name === panel.name)) {
      const parent = panels.find((p: Panel) =>
        p.children?.find((c: Panel) => c.name === panel.name),
      );
      return {
        activePanel: panel.name,
        activeParent: parent?.name ?? '',
      };
    } else {
      return {
        activePanel: panel.name,
        activeParent: '',
      };
    }
  }

  findPanelByName(panels: Panel[], panelName: string): Panel | undefined {
    const topLevelPanel = panels.find((p) => p.name === panelName);
    if (topLevelPanel) {
      return topLevelPanel;
    }

    for (const panel of panels) {
      if (panel.children) {
        const childPanel = panel.children.find((child) => child.name === panelName);
        if (childPanel) {
          return childPanel;
        }
      }
    }

    return undefined;
  }

  getUrlParam(name: string) {
    if (typeof window === 'undefined') return null;

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    return urlParams.get(name);
  }

  getHashFragment() {
    // Extract hash fragment from URL (e.g. "nfl" from "#nfl")
    return window.location.hash.slice(1);
  }

  getActivePanelAndParentFromUrl(
    panels: Panel[],
    params: ParamMap,
  ): { activePanel: string; activeParent: string; shouldScrollToPanelNav: boolean } | undefined {
    const noHashFragment = this.getHashFragment() === '';
    if (params.get(PANEL_ROUTE_PARAMS.SUBTAB)) {
      const parent = panels.find((p: Panel) => p.name === params.get(PANEL_ROUTE_PARAMS.TAB));
      const child = parent?.children?.find(
        (c: Panel) => c.name === params.get(PANEL_ROUTE_PARAMS.SUBTAB),
      );
      if (!parent || !child) return undefined;
      return {
        activePanel: child.name,
        activeParent: parent.name,
        shouldScrollToPanelNav: noHashFragment,
      };
    } else if (params.get(PANEL_ROUTE_PARAMS.TAB)) {
      const panel = panels.find((p: Panel) => p.name === params.get(PANEL_ROUTE_PARAMS.TAB));
      if (panel) {
        const { activePanel, activeParent } = this.getActivePanelAndParent(panels, panel);
        return {
          activePanel,
          activeParent,
          shouldScrollToPanelNav: noHashFragment,
        };
      }
    }
    return undefined;
  }

  getFallbackPanelIfInvalid(
    panels: Panel[],
    activePanel: string,
  ): { activePanel: string; activeParent: string } | undefined {
    const currentPanel = this.findPanelByName(panels, activePanel);
    if (!currentPanel || currentPanel.disabled) {
      const fallbackPanel = panels.find((panel) => panel.disabled === false);
      if (fallbackPanel) {
        return this.getActivePanelAndParent(panels, fallbackPanel);
      }
    }
    return undefined;
  }

  /**
   * Resolves the panel to show on a details page's first load.
   * If no panel is specified in the URL, the default panel is used and the URL is left untouched,
   * retaining all query parameters and hash fragments.
   * If a disabled or invalid panel is specified, the default panel is used and `replacementUrl` is
   * the base path with only `queryParams`, dropping the invalid panel, other query parameters, and
   * the hash fragment.
   */
  resolveInitialPanel(
    panels: Panel[],
    params: ParamMap,
    basePath: string,
    queryParams: QueryParams = {},
  ): {
    activePanel: string;
    activeParent: string;
    shouldScrollToPanelNav: boolean;
    replacementUrl?: string;
  } {
    const fromUrl = this.getActivePanelAndParentFromUrl(panels, params) ?? {
      activePanel: '',
      activeParent: '',
      shouldScrollToPanelNav: false,
    };
    const fallback = this.getFallbackPanelIfInvalid(panels, fromUrl.activePanel);
    if (!fallback) return fromUrl;

    if (!params.has(PANEL_ROUTE_PARAMS.TAB)) {
      return { ...fallback, shouldScrollToPanelNav: false };
    }
    return {
      ...fallback,
      shouldScrollToPanelNav: false,
      replacementUrl: this.appendQueryParams(basePath, queryParams),
    };
  }

  encodeParenthesesForwardSlashes(uri: string) {
    return (
      uri
        // forward slash within parentheses
        .replace(/\(([^)]*)\)/g, (match, content) => `(${content.replace(/\//g, '%2F')})`)
        // parentheses
        .replace(/\(/g, '%28')
        .replace(/\)/g, '%29')
    );
  }

  getNumberFromCSSValue(cssValue: string) {
    return parseFloat(cssValue);
  }

  getSignificantFigures(n: number, sig = 2) {
    let sign = 1;
    if (n === 0) {
      return 0;
    }
    if (n < 0) {
      n *= -1;
      sign = -1;
    }

    const mult = Math.pow(10, sig - Math.floor(Math.log(n) / Math.LN10) - 1);
    return (Math.round(n * mult) / mult) * sign;
  }

  cleanFilename(filename: string) {
    const invalidFilenameCharsRegex = /[<>:"\\/|?*]/g;
    return filename.replaceAll(invalidFilenameCharsRegex, '_').replaceAll(' ', '_');
  }
}
