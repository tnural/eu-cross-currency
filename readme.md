

### Requirements
- Jdk 18
- Gradle 7.4.1
- Lombok IDE configuration (if needed)

### How to run
- $**_./gradlew bootRun_** run command in the root of project to start web server.
- $**_./gradlew clean test_** run command in the root of project to run the tests.

### Dockerize application
- **./gradlew clean build** build project in the root.
- **docker build -t takehometask/scalablecapital .** build docker image.
- **docker run -p 8080:8080 takehometask/scalablecapital** run docker image.


### Request examples

##### Requirement 1
`curl --location --request GET 'localhost:8080/currencyrate?from=USD&to=EUR`

`curl --location --request GET 'localhost:8080/currencyrate?from=EUR&to=USD`


##### Requirement 2
`curl --location --request GET 'localhost:8080/currencyrate?from=HUF&to=USD`


##### Requirement 3

`curl --location --request GET 'localhost:8080/currencyrate'`

##### Requirement 4 (change date)

`curl --location --request GET 'localhost:8080/currencyrate/convert' \
--header 'Content-Type: application/json' \
--data-raw '{
"currency":{
"fromCurrency": "HUF",
"toCurrency": "TRY",
"date": "2022-05-09"
},
"amount":55.2
}'`

##### Requirement 5 (change date)
`
curl --location --request GET 'localhost:8080/currencyrate?from=USD&to=EUR&date=2022-05-09'`