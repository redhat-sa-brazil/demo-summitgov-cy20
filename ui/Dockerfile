FROM node:10
ENV MYUI_USER myuiuser

COPY package*.json ./

RUN npm install

COPY . .

# Build frontend JS assets
RUN npm run build

# Remove our "devDependencies" since they're required for build, but not runtime
RUN npm prune --production

EXPOSE 8080

USER ${MYUI_USER}

CMD [ "npm", "start" ]

