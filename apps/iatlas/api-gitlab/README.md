# Example Flask+SQLAlchemy Project

This example project demos integration between Graphene, Flask and SQLAlchemy.
The project contains two models, one named `Department` and another
named `Employee`.

## Getting started

The following command will setup the database, and start the server:

```bash
./start.sh
```

To start the server run the following command in the promt created by the previous command (`bash-5.0#` - in the Docker container):

```bash
./app/app.py
```

The following command will stop the server:

```bash
./stop.sh
```

Now head on over to
[http://localhost:5000/graphiql](http://localhost:5000/graphiql)
and run some queries!
