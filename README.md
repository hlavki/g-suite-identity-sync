# web-client

> xIT Access Account Manager

## Build Setup

``` bash
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# build for production with minification
npm run build

# build for production and view the bundle analyzer report
npm run build --report

# run unit tests
npm run unit

# run all tests
npm test
```

For detailed explanation on how things work, checkout the [guide](http://vuejs-templates.github.io/webpack/) and [docs for vue-loader](http://vuejs.github.io/vue-loader).


/usr/java/default/bin/keytool -genkeypair -keyalg RSA -validity 3650 \
 -alias account-server \
 -dname "cn=account, ou=xIT s.r.o., o=xIT Account Manager, C=SK, L=Bratislava, S=Slovakia" \
 -keypass changeit -storepass changeit \
 -keystore account-manager.jks \
 -ext SAN=DNS:account.xit.camp,DNS:account.xit.camp

/usr/java/default/bin/keytool -exportcert -alias account-server -storepass changeit -keystore account-manager.jks -rfc -file account-server.pem

# Links
https://developers.google.com/identity/protocols/OAuth2WebServer#protectauthcode

offline
https://developers.google.com/identity/sign-in/web/server-side-flow

http://justincalleja.com/2016/04/17/serving-a-webpack-bundle-in-spring-boot/
