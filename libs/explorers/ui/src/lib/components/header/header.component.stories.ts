import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { Component } from '@angular/core';
import { provideRouter } from '@angular/router';
import {
  mockCheckQueryForErrors,
  mockGetSearchResults,
  mockNavigateToResult,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { SearchInputComponent } from '../search-input/search-input.component';
import { HeaderComponent } from './header.component';

@Component({ template: '', standalone: true })
class DummyComponent {}

const meta: Meta<HeaderComponent> = {
  component: HeaderComponent,
  title: 'UI/Header',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([{ path: '**', component: DummyComponent }]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<HeaderComponent>;

const withSearchInput = (args: Record<string, unknown>) => ({
  props: args,
  template: `
    <explorers-header
      [headerLogoPath]="headerLogoPath"
      [headerLinks]="headerLinks"
    >
      <explorers-search-input
        searchPlaceholder="Search genes"
        [navigateToResult]="navigateToResult"
        [getSearchResults]="getSearchResults"
        [checkQueryForErrors]="checkQueryForErrors"
      />
    </explorers-header>
  `,
  moduleMetadata: {
    imports: [HeaderComponent, SearchInputComponent],
  },
});

export const Header: Story = {
  render: (args) =>
    withSearchInput({
      ...args,
      navigateToResult: mockNavigateToResult,
      getSearchResults: mockGetSearchResults,
      checkQueryForErrors: mockCheckQueryForErrors,
    }),
  args: {
    headerLogoPath: 'model-ad-assets/images/header-logo.svg',
    headerLinks: [
      { label: 'Home', routerLink: ['/'], activeOptions: { exact: true } },
      {
        label: 'Menu',
        children: [
          { label: 'Item A', routerLink: ['/menu/a'] },
          { label: 'Item B', routerLink: ['/menu/b'] },
        ],
      },
      {
        label: 'Menu (subheaders)',
        children: [
          {
            label: 'Group One',
            isSubheader: true,
            children: [
              { label: 'Item A', routerLink: ['/grouped/one/a'] },
              { label: 'Item B', routerLink: ['/grouped/one/b'] },
            ],
          },
          {
            label: 'Group Two',
            isSubheader: true,
            children: [{ label: 'Item A', routerLink: ['/grouped/two/a'] }],
          },
        ],
      },
      { label: 'About', routerLink: ['/about'] },
    ],
  },
};
