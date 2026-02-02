import { Component } from '@angular/core';
import type { Meta, StoryObj } from '@storybook/angular';

@Component({
  selector: 'lib-welcome',
  standalone: true,
  template: `
    <div style="font-family: sans-serif; padding: 2rem; max-width: 800px;">
      <h1>Component Storybooks</h1>
      <p>
        This Storybook instance aggregates stories from all domains in the Sage monorepo using
        Storybook Composition.
      </p>

      <h2>Available Storybooks</h2>
      <ul>
        <li><strong>Agora</strong> - AD research platform components</li>
        <li><strong>Explorers</strong> - Data exploration and visualization components</li>
      </ul>

      <h2>Local Development</h2>
      <p>Start the composition storybook (automatically starts all child storybooks):</p>
      <pre style="background: #f5f5f5; padding: 1rem; border-radius: 4px; overflow-x: auto;">
<code># Start composition with all child storybooks
nx storybook storybook            # http://localhost:4400</code>
      </pre>

      <p>Or start individual storybooks separately:</p>
      <pre style="background: #f5f5f5; padding: 1rem; border-radius: 4px; overflow-x: auto;">
<code># Agora storybook only
nx storybook agora-storybook      # http://localhost:4401

# Explorers storybook only
nx storybook explorers-storybook  # http://localhost:4402</code>
      </pre>

      <h2>Learn More</h2>
      <ul>
        <li>
          <a href="https://storybook.js.org/docs/sharing/storybook-composition" target="_blank"
            >Storybook Composition Documentation</a
          >
        </li>
        <li><a href="https://nx.dev/recipes/storybook" target="_blank">Nx Storybook Guide</a></li>
      </ul>
    </div>
  `,
})
class WelcomeComponent {}

const meta: Meta<WelcomeComponent> = {
  title: 'Welcome',
  component: WelcomeComponent,
};

export default meta;

type Story = StoryObj<WelcomeComponent>;

export const Introduction: Story = {};
