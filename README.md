# Binance order book metrics challenge

## About
Challenge program gives interested individuals a chance to prove themselves and learn a bit about
the company & products. Our challenges are extremely independent and will require you to manage 
your own time and work process.

## Description
This application requests order books for all Binance symbols (financial instruments)
and calculates the average volumes for asks and bids.

You can find a short description of order book [here](https://en.wikipedia.org/wiki/Order_book). 

## How it works
We use the following technologies in this application:
- Kotlin lang
- Spring Boot WebFlux framework
- Maven for dependency management and build

This app has a standard structure for Spring Boot:
- `application.yml` configuration file
- `KotlinChallengeApplication` application entry point
- Spring configuration in `com.inca.challenge.config` package
- Binance REST API client and model in `com.inca.challenge.api` package
- `OrderBookMetricsCalculator` order book metrics calculator job
- `OrderBookMetricsCalculatorTest` Spring Boot test to start application locally

In `OrderBookMetricsCalculator` job implemented the following algorithm:
1. retrieve all symbols and rate limits from Binance API
2. for all symbols one by one retrieve order books 
3. calculate the average volume for asks and bids
4. once above is done, print metrics and statistics to log and finish the application

The application works correctly, except for timeout in `OrderBookMetricsCalculator`.

## How to run
Application only requires the JVM to be installed and can be launched with
```shell
./mvnw test
```

## What to do
We expect the order book metrics calculator to be improved to fit app timeout and 
reduced memory consumption.

The first problem that it takes too long to retrieve order books one by one. This 
can be optimized if you make calls in parallel. But be careful to not exceed Binance
API rate limits. You can find how heavy is every API call in 
[docs](https://binance-docs.github.io/apidocs/spot/en/#order-book). You can also use 
`BinanceApiClient#getRateLimits` to track currently available limits.

Even after fixing the first problem, the calculator is not ready to become a real world
streaming application as it keeps all the state in memory to compute the average volumes.
Therefore, as a next step we want you to optimize memory consumption.

Please make minimal changes to the `OrderBookMetricsCalculator` class only.

## What's next
If you are ready with your branch, create a pull request and wait for review. As soon as
we get a good enough solution from a candidate, we start the interviewing process.
