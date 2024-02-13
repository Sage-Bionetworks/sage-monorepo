Often times, it can be quite time-consuming to go from design concepts to code. With the Challenge
Registry, we hope to alleviate this hurdle by utilizing TeleportHQ's Figma-to-code capability.
[TeleportHQ] is an online collaborative platform meant for front-end development, in which a static
website can either be built from scratch, or, in our case, from pre-defined components and frames
created in Figma.

## Requirements

* Figma account (free plan)
* TeleportHQ account (free plan)

> **Note** Paid plans are provided for both tools at Sage upon request. Please submit a [Jira ticket
> to Sage IT] to request a Figma and/or TeleportHQ license (tag Jake Albrecht for approval).
> Otherwise, you are allowed up to 3 Figma files and 3 TeleportHQ projects when using the basic
> (free).

> **Note** You will need Edit access to a Figma document in order to use TeleportHQ plugin (see
> below). If you don't have Edit access to a Figma document that you want to export, make a copy of
> the Figma document first, then export the copy to TeleportHQ.

## Getting started

This guide should work with both the web and desktop Figma app.

1. Download the [TeleportHQ plug-in] in Figma (this step is only needed once).

2. Create components and frames in Figma as needed.  If a component or frame uses any color and/or
text styles, these will also be included in the export. Learn more about creating color and text
styles [here].

3. When ready, select a frame or component to include in the export.  This is easiest to do from the
Layers panel. The export can include multiple components and frames -- hold the `cmd` key on a Mac
or `shift` key on a Windows to select your desired layers.

4. Right-click on the selected layer(s) from the Layers panel > Plugins > TeleportHQ - Figma to
   Code.

5. A pop-up window will be displayed, where you can review the layers, styles, and config that will
be included in the export. Used styles will be highlighted in blue.

6. If you already have a TeleportHQ project defined, you can select **Copy Layers to Clipboard** to
manually copy over the layers to the existing project.  Otherwise, click on
**Export as a new project**.  Depending on how many layers have been selected, this can take
anywhere from a couple of seconds to 1-2 minutes; you will then be taken to the TeleportHQ platform.

## Navigating TeleportHQ

> **Note** This section is inspired by [TeleportHQ's docs]. Some parts are copied over verbatim.

### Your workspace

When you enter a project in TeleportHQ, your workspace will consist of three panels.

#### Left panel
By default, the left panel will display **Layers & Files**. This is further divided into two
sections, where the top half will display the pages and components of the project, and the bottom
half will display the layers of the selected file/component.

Other options for the left panel include:
* **Elements**: the basic building blocks with which you can start building your user interfaces.
Most of them correspond to native HTML elements, e.g. Row, Column.  You can drag and drop an element
from this panel into the Canvas, or use their keyboard shortcuts.  See their lesson on [Elements] to
learn more.
* **CSS Classes**: sets of styles that can be reused on multiple elements.  TeleportHQ will include
some by default.  You can create, search, or edit classes as needed.  See their lesson on
[CSS Classes] to learn more.
* **Asset manager**: allows you to upload your own assets, access directly tens of thousands of
images from the Unsplash, and choose icons from several popular libraries.

#### Middle panel
This is your **Canvas**, aka where all of the visual building ✨ magic ✨ happens.

#### Right panel
When an element is not selected, the right panel will display the **Design Language Panel**. This is
where you can explore and define style tokens used throughout the project.  TeleportHQ includes some
styles by default, e.g. "Primary" for colors. If your Figma export included color palettes, they
will be listed under "Default" in the Color tab. Similarly, if your Figma export included text
styles, they will be listed individually in the Text tab.

When an element _is_ selected, the right panel will change to the **Style Inspector Panel**, where
you can add styles to the elements and/or edit the.

> **Note** All tokens defined in the **Design Language Panel** will be included in the final code
> export.  Feel free to add/modify/remove tokens as needed, so that the code export only has the
> definitions you need.

### Prepare the export

Before exporting to code, we recommend reviewing the Figma-exported elements first, as the
Figma-to-TeleportHQ export is not always perfect.

For example, let's say you created a set of layers in Figma that is intended to be a button, that
is, the layer group includes a shape, a color fill, and some text.  When this gets exported to
TeleportHQ, it becomes as a group of layers that are an image (the color fill) and a text block.
This  subsequently gets exported as `<img>` and `<span>` in the code, which may not be what you
intended.

Instead, remove the image layer, then apply a CSS class to the parent group.  If you are using one
of TeleportHQ's pre-defined CSS classes, you can edit it from the **CSS Classes** panel. You can
alternatively use TeleportHQ's Button element from their **Elements** panel, and replace the
"button" layers with it.

If there is time or if you are not familiar with editing HTML & CSS, we recommend applying a layout
to the Figma-exported project, e.g. applying the Row and Column elements.  Additionally, if
responsive design is important in the final product, we recommend using relative sizing and media
queries.  See their lesson on [Responsive design] to learn more.

Otherwise, you are more than welcome to add/edit the layout and responsiveness in the exported code.

## Exporting the project

1. In the top-right corner, select **Export Project** (the icon next to the Publish button).

2. Select **Angular** or **HTML** as the library, whichever you are more comfortable with.  Note
that exporting as **Angular** will download the files as a standalone app!

3. Click on the file icon at the bottom of the dropdown to download the project as a zip.

## Next steps

After creating a new library and component within the app, copy-paste code from the exported project
into the app.  See [create-a-new-angular-component] for more details on how to create the library and
component, and where to copy-paste the code.


[TeleportHQ]: https://teleporthq.io/
[Jira ticket to Sage IT]: https://sagebionetworks.jira.com/jira/software/c/projects/IT/boards/146
[create-a-new-angular-component]: https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/docs/create-a-new-angular-component.md
[TeleportHQ plug-in]: https://www.figma.com/community/plugin/992726161890204477
[here]: https://help.figma.com/hc/en-us/articles/360038746534-Create-color-text-effect-and-layout-grid-styles
[TeleportHQ's docs]: https://help.teleporthq.io/en/
[Elements]: https://help.teleporthq.io/en/category/elements-ekqxm7/
[CSS Classes]: https://help.teleporthq.io/en/article/css-classes-zxd7vm/
[Responsive design]: https://help.teleporthq.io/en/article/responsive-design-7o4mb6/
