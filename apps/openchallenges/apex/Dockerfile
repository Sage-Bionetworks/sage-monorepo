FROM nginx:1.25.0

COPY templates /etc/nginx/templates/
COPY nginx.conf /etc/nginx/

EXPOSE 8000

CMD ["nginx", "-g", "daemon off;"]