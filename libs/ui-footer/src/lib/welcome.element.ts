const template = document.createElement('template');
template.innerHTML = `<style>
    h1 {
      color: red;
    }
  </style>
  <h1>Welcome to <span id="title"></span>!</h1>`;

export class WelcomeElement extends HTMLElement {
  public static observedAttributes = ['title'];

  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
    this.shadowRoot?.appendChild(template.content.cloneNode(true));
  }

  attributeChangedCallback() {
    const element = this.shadowRoot?.getElementById('title');
    if (element) {
      element.innerHTML = this.title;
    }
  }
}

customElements.define('ui-welcome', WelcomeElement);
