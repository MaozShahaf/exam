# Luxon Assignment 
This project demonstrates a simple incomplete exchange application

### Installation
In order to work local you will need JAVA 11 JDK , you can find a proper version [here](https://www.oracle.com/ie/java/technologies/javase-jdk11-downloads.html)
Use the package manager [gradle](https://gradle.org/install/) to install the relevant dependencies.

```bash
./gradlew clean install
```

### What should I focus during this assignment ?
The main concern should be **transactional programming** with respect 
to other relevant aspects regarding exchange and [FIX](https://en.wikipedia.org/wiki/Financial_Information_eXchange) systems such **error handling**, **atomic operations**,**locks**, **security measures** etc.

### What should I implement
first, there is no only one proper solution. any kind of solution which covers most/all the scenarios can be good enough.
there is a two main places you should focus on: ExchangeService (_exchange_ method), and the ExchangeRequestDto which missing some fields that needs to be added by you.
your target is to implement buy,sell,send transaction types.
for example:
```bash
a user can buy BTC with USD,EUR or GBP
a user can sell BTC for USD,EUR or GBP
a user can buy ETH for USD,EUR or GBP
```

Your top priority should be implement **Buy** and **Send** scenarios. (we would like to see you implement Sell scenario as well!)
