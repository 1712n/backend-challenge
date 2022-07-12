# Task description

In this application we are trying to get all order books for all possible symbols from binance and calculate the average volume of all asks.

In OrderBookAskVolumeAverageCalculator class has implemented the following algorithm:
It includes 3 steps:
1. http call for symbols
2. http call for available weight limits.
3. http calls for order books (expected to be speed up)
4. calculate the average volume of all asks (expected to be reduced the memory consumption)

We expect that the last two steps can be significantly improved.
Your task is to speed up the step 3 and reduce the memory consumption of step 4.