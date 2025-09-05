FROM nginx:latest

# Remove default config and add ours
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

# Copy static files
COPY site/ /usr/share/nginx/html/

# Nginx listens on 80 in the container
EXPOSE 80

# (nginx:alpine uses the right CMD already)
