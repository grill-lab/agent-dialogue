# pull official base image
FROM node:latest as builder
# set working directory
WORKDIR /app
# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH
# install app dependencies
COPY package.json ./
RUN npm install --silent
# add app
COPY . ./
# start app
CMD ["npm", "start"]

FROM nginx:latest
COPY --from=builder /app/dist/chat /usr/share/nginx/html
EXPOSE 80