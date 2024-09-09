# Search Engine and Social Media Optimization

## Create local HTTP tunnel with ngrok

1. Create an [ngrok] account.
2. Copy your token from Getting Started > Your Authtoken.
3. Install the ngrok client.
4. Authenticate witn your ngrok token.
   ```console
   ngrok config add-authtoken <token>
   ```
5. Start the HTTP tunnel, for example for the Angular app that listens to the port 4200.
   ```console
   ngrok http --host-header=rewrite 4200
   ```

## Curl a page as a bot

We can send the name of the agent - here `Twitterbot` - to the server. This information could be
used by the server during dynamic rendering.

```console
curl -A Twitterbot http://localhost:4200
```

<!-- Links -->

[ngrok]: https://ngrok.com/
