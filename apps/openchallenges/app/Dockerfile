# Serve app with nginx server
FROM nginx:1.23.2-alpine

COPY dist/apps/openchallenges/app/browser /usr/share/nginx/html
COPY docker/nginx /etc/nginx/.
COPY docker/10-envsubst-on-app-config-template.sh /docker-entrypoint.d/.

RUN chmod +x /docker-entrypoint.d/10-envsubst-on-app-config-template.sh

EXPOSE 4200

CMD ["nginx", "-g", "daemon off;"]
