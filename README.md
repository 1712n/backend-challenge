# Binance order book metrics challenge

## Overview
[Challenge program](https://github.com/1712n/challenge) gives interested individuals a chance to prove themselves and learn a bit about
the company & products. Our challenges are extremely independent and will require you to manage your own time and work process.

### Process
1. Submit your challenge as described in [Workflow](README.md#workflow).
1. In case your submission is approved, one of our team members will reach out to set up 30-minute Google Meet orietnation call. Successful candidates will be offered 1.5 hrs technical interview.
1. Candidates that made it to the final stage will be offered a contract with [Inca Digital](https://inca.digital/).

### Required skills:
- Java/Kotlin programming language
- understanding of stream and reactive programming approaches

## Description
This application requests order books for all Binance symbols (financial instruments)
and calculates the average volumes for asks and bids.

You can find a short description of order book [here](https://en.wikipedia.org/wiki/Order_book). 

We use the following technologies:
- Kotlin lang
- Spring Boot WebFlux framework
- Maven for dependency management and build

This app has a standard structure for Spring Boot:
- `application.yml` configuration file
- `ChallengeApplication` application entry point
- Spring configuration in `com.inca.challenge.config` package
- Binance REST API client and model in `com.inca.challenge.api` package
- `OrderBookMetricsCalculator` order book metrics calculator job
- `OrderBookMetricsCalculatorTest` Spring Boot test to start application locally

In `OrderBookMetricsCalculator` implemented the following algorithm:
1. retrieve all symbols and rate limits from Binance API
2. for all symbols one by one retrieve order books 
3. calculate the average volume for asks and bids
4. once above is done, print metrics and statistics to log and finish the application

The application works correctly, except for timeout in `OrderBookMetricsCalculator`.

## Problem
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

Please make minimal changes to the `OrderBookMetricsCalculator` class only. If you prefer
Java lang, feel free to convert calculator class from Kotlin to Java.

## Workflow
1. Clone this repository and push it to your **private** repository named `<OWNER>/inca-backend-challenge`
2. From `Settings` menu create Actions secret `PROXY_SETTINGS` with proxy settings (e.q `-DsocksProxyHost=158.69.225.110 -DsocksProxyPort=59166`). See free proxies [list](http://free-proxy.cz/en/proxylist/country/all/socks5/ping/all)
3. Create a Pull Request with all your changes into the `main` branch in your new repository
4. Make sure the Pull Request `Run tests` GitHub Action check successfully passed
5. From `Settings` menu add @alekseypolukeev and @iliagon to collaborators, we will review your code and get back to you

## Test locally
Application only requires the JVM to be installed and can be launched with
```shell
./mvnw test
```

For the testing purposes you can decrease the number of handled symbols using dry-run mode:
```shell
./mvnw test -Dbinance.api.order-book.dry-run=true
```

As Binance API can't be accessed from US (451 Unavailable For Legal Reasons), you can use any free HTTP/HTTP/SOCKS proxy:
```shell
./mvnw test -DsocksProxyHost=158.69.225.110 -DsocksProxyPort=59166
```
More options can be found [here](https://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html).
