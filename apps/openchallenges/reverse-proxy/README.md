# OpenChallenges Reverse Proxy

## Follow access log

```console
docker exec -it openchallenges-reverse-proxy \
  tail -f /var/log/nginx/openchallenges-reverse-proxy.access.log
```