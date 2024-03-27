# iAtlas API Profiling

[BACK TO MAIN README](./../../README.md)

## Deterministic Profiling

Functions may be profiled using the `@profile(__name__)` decorator. Adding this decorator to a function will cause the app to write a profile for that function when it is called. The profile may be reviewed via [SnakeViz](https://jiffyclub.github.io/snakeviz/). Simply call:

```sh
./view_profile.sh
```

from the root of the project. A list of profile options will be given. Once a profile is selected, the SnakeViz server will render the profile as a webpage. The webpage URL will be displayed in the console. Go to the page in your browser to view the profile.
By default, SnakeViz runs on port `8020`. To change this port, set the SNAKEVIZ_PORT variable in a `.env-dev` file in the root of the project (see the `.env-SAMPLE` for an example.)
