export class WelcomeElement extends HTMLElement {
  public static observedAttributes = ['title'];

  attributeChangedCallback() {
    this.innerHTML = `<h1>Welcome ${this.title}</h1>`;
  }
}

// Name for the new custom element. Note that custom element names must contain
// a hyphen.
customElements.define('ui-welcome', WelcomeElement);
