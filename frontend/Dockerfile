FROM node:18-alpine as builder

ARG VITE_GOOGLE_MAPS_API_KEY
ENV VITE_GOOGLE_MAPS_API_KEY=$VITE_GOOGLE_MAPS_API_KEY

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN  npm run build

FROM nginx:alpine AS runner

WORKDIR /usr/share/nginx/html

RUN rm -rf ./*

COPY --from=builder /app/dist ./

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]