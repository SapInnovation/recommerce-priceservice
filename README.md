# Build Reactive Rest APIs with Spring WebFlux and Reactive MongoDB


## Requirements

1. Java - 1.8.x

2. Maven - 3.x.x

3. MongoDB - 3.x.x

## Steps to Setup

**1. Clone the application**

```bash
git clone git@github.com:SapInnovation/recommerce-priceservice.git
```

**2. Build and run the app using gradle**

```bash
cd recommerce-priceservice
./gradlew build
java -jar target/recommerce-price-0.0.1-SNAPSHOT.jar
```

The server will start at <http://localhost:8080>.

## Exploring the Rest APIs

The application defines following REST APIs

```

1. GET /price/{productId} - Retrieve a price by Id

2. PUT /price/{productId} - Update a price

3. DELETE /price/{productId} - Delete a price

4. GET /stream/price - Stream price to a browser as Server-Sent Events
```

## Running integration tests

