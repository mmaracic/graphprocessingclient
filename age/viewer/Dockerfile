# syntax=docker/dockerfile:1
FROM node:14-alpine3.13
WORKDIR /app
COPY . .
RUN unzip age-viewer-1.0.0-rc2.zip
RUN mv age-viewer-1.0.0-rc2 viewer
WORKDIR /app/viewer

RUN apk --no-cache add bash
RUN set -eux; \
    apk --no-cache --virtual .build-deps add git less openssh; \
    \
    cd /app/viewer/frontend; \
    yarn install; \
    yarn run build; \
    ls -A | grep -v 'build' | xargs rm -rf; \
    \
    cd /app/viewer/backend; \
    yarn install; \
    yarn run build; \
    ls -A | egrep -v "^(build|package|node_modules|sql)" | xargs rm -rf; \
    \
    cd /; \
    rm -rf $(yarn cache dir); \
    apk del .build-deps

RUN chmod +x docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]
EXPOSE 3001

