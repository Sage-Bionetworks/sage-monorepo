# Data Version Control

## Overview

## Add a file with DVC

```console
$ dvc add sage.png
100% Adding...|████████████████████████████████████████████|1/1 [00:00, 24.41file/s]

To track the changes with git, run:

        git add sage.png.dvc

To enable auto staging, run:

        dvc config core.autostage true
```

Now you can push data from your local machine to the AWS remote. First, add the data you want DVC to
track with the following command:

```console
$ git add sage.png.dvc
```

## References