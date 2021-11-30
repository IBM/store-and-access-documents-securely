FROM registry.access.redhat.com/ubi8/nodejs-14:1-28 AS builder

USER root
RUN mkdir /app
WORKDIR /app
COPY server.js /app
COPY package.json /app
COPY .env /app
WORKDIR /app/ui-react/build
COPY ./ui-react/build .

WORKDIR /app
RUN npm install

# Build our deployable image based on UBI
FROM registry.access.redhat.com/ubi8/nodejs-14:1-28

USER 1001
WORKDIR /app
COPY --from=builder /app .

ENV WEB_PORT 3001
EXPOSE  3001

# Define command to run the application when the container starts
CMD ["node", "/app/server.js"]