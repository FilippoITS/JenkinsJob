# Stage di build
FROM node:18 AS build-stage

WORKDIR /app

COPY src/job-app/package*.json ./
RUN npm install

COPY src/job-app/ ./

RUN npm run build

# Stage per Nginx
FROM nginx:1.15

# Copia la build di React
COPY --from=build-stage /app/build /usr/share/nginx/html

# Copia il file di configurazione personalizzato di Nginx
COPY templates/back-end/src/job/config/default.conf ./default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
