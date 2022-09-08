# Ngrok

## Create local HTTP tunnel for Ng apps

```console
ngrok config add-authtoken <token>
ngrok http --host-header=rewrite 4200
```

## Preview the page that Twitterbot see

We can send the name of the agent - here `Twitterbot` - to the server. This information could be
used by the server during dynamic rendering.

```console
curl -A Twitterbot http://localhost:4200
```