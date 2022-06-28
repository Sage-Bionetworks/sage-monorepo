# draw.io Resources

## Overview

[draw.io] (also known as diagrams.net) is a free and open source cross-platform
graph drawing software to create diagrams such as flowcharts, wireframes, UML
diagrams, organizational charts, and network diagrams.

[draw.io]'s integration with GitHub, the fact that it is an open-source project
and several quality-of-life features (e.g., ability to set shape connector
points and edit path waypoints) make it the preferred tool to create diagrams
for this project.

## Workflow

The same workflow applies to the development of code and diagrams. See
[CONTRIBUTING.md](.github/CONTRIBUTING.md) for more information on contributing
and/or our Forking Workflow approach.

Remainders:

- Create a new branch in your fork whenever you create or edit a diagram.
- From draw.io, save the diagram to this branch in your fork.
- Submit the new or updated diagram via a Pull Request.

## Naming conventions

- Diagram filename
  - Diagram names must be lower kebab case.
  - Use the extension `.drawio.svg` to enable GitHub to render a preview of the
  diagram, while indicating that this is a draw.io file.

## Create a diagram in draw.io

1. Open your browser and navigate to [draw.io].
2. Log in with your GitHub account.
3. To create a diagram click on *File* > *New...*.
4. Select the extension `Editable Vector Image (.svg)`.
5. Enter the name of the diagram and use the extension `.drawio.svg`.
6. Click on `Create`.

## Save a diagram to draw.io

If you are saving a file that has been opened from GitHub,

1. Click on *File* > *Save* (*Ctrl + S*)
2. Specify a commit message and click on *OK*.

Otherwise,

1. Click on *File* > *Save as...*.
2. Check that the filename has the extension `.drawio.svg`.
3. Click on `GitHub`.
4. Enter Value (org/repo/ref): `<gh_username>/challenge-registry/<branch_name>`
   and click *OK*.
5. Select the folder `docs/drawio/diagrams` and click on *OK*.

## Open a diagram

1. To open a diagram that you have recently open, click on *File* > *Open
   Recent* and select the diagram.
2. Otherwise, click on *File* > *Open from* > *GitHub...*.
3. Enter Value (org/repo/ref): `<gh_username>/challenge-registry/<branch_name>`
   and click *OK*.
4. Select a diagram from the folder `docs/drawio/diagrams` and click on *OK*.

## Open a project library

1. Click on *File* > *Open Library from...* > *GitHub...*.
2. Enter Value (org/repo/ref): `<gh_username>/challenge-registry/<branch_name>`
   and click *OK*.
3. Select a library from the folder `docs/drawio/libraries` and click on *OK*.

## Add an icon to a library

1. Download the icon in svg format that you want to add from the official
   website.
   - If an official icon in svg format cannot be found, it is accepted to use an
     unofficial icon as long as the icon looks identical to the official icon.
   - If an icon can not be found in svg format, it is accepted to create an svg
     icon from a bitmap image.
2. Verify that the icon meets the following requirements.
   - The height of the icon must be 80 pt.
   - The height and width of the svg document must fit the icon.
   - The background of the scg document must be transparent.
3. Open the existing library in draw.io.

> **Note** Import the library from a feature branch that you have created in
> your fork so that you can submit the new icon in a PR.

4. Drag and drop the svg file to the library.
5. Click on the `Save` button that appeared in the header of the library.
6. Submit a PR to the upstream repository.

## Tips

- [Edit shape fixed connector points](https://drawio-app.com/connection-points-functionality-and-customization-in-project-management/)
- [Edit path waypoints](https://drawio-app.com/waypoints-in-draw-io-building-a-path-for-your-connectors/)

<!-- Links -->

[draw.io]: http://draw.io