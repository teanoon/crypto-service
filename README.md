# How to run
1. `./gradlew clean build bootRun`

# Output sample
```log
2021-11-29 02:29:30.251  INFO 1087027 --- [nPool-worker-51] c.e.c.c.CryptoServiceImpl                : get batch 200
2021-11-29 02:29:31.348  INFO 1087027 --- [nPool-worker-51] c.e.c.c.CryptoServiceImpl                : get batch 200
2021-11-29 02:29:32.515  INFO 1087027 --- [nPool-worker-51] c.e.c.c.CryptoServiceImpl                : get batch 200
2021-11-29 02:29:33.564  INFO 1087027 --- [nPool-worker-51] c.e.c.c.CryptoServiceImpl                : get batch 200
2021-11-29 02:29:34.653  INFO 1087027 --- [nPool-worker-51] c.e.c.c.CryptoServiceImpl                : get batch 200
2021-11-29 02:29:35.774  INFO 1087027 --- [nPool-worker-51] c.e.c.c.CryptoServiceImpl                : get batch 200
2021-11-29 02:29:35.776  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : actual: 27302546, CandleStick(timestamp=1638152760000, openPrice=57615.43, closePrice=57589.35, highPrice=57617.79, lowPrice=57579.17, volume=13.252473)
2021-11-29 02:29:35.776  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : actual: 27302547, CandleStick(timestamp=1638152820000, openPrice=57589.19, closePrice=57540.24, highPrice=57589.19, lowPrice=57523.52, volume=15.560353)
2021-11-29 02:29:35.777  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : actual: 27302548, CandleStick(timestamp=1638152880000, openPrice=57544.27, closePrice=57503.7, highPrice=57547.69, lowPrice=57487.55, volume=28.907449)
2021-11-29 02:29:35.777  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : actual: 27302549, CandleStick(timestamp=1638152940000, openPrice=57503.71, closePrice=57498.82, highPrice=57529.86, lowPrice=57491.24, volume=34.349412)
2021-11-29 02:29:35.910  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : expected: 27302546, CandleStick(timestamp=1638152760000, openPrice=57600.31, closePrice=57589.35, highPrice=57617.79, lowPrice=57579.17, volume=28.074687)
2021-11-29 02:29:35.910 ERROR 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : Candle stick doesn't match trades at 1638152760000
2021-11-29 02:29:35.910  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : expected: 27302547, CandleStick(timestamp=1638152820000, openPrice=57589.19, closePrice=57540.24, highPrice=57589.19, lowPrice=57523.52, volume=15.560353)
2021-11-29 02:29:35.910  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : Candle stick matches trades at 1638152820000
2021-11-29 02:29:35.910  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : expected: 27302548, CandleStick(timestamp=1638152880000, openPrice=57544.27, closePrice=57503.7, highPrice=57547.69, lowPrice=57487.55, volume=28.907449)
2021-11-29 02:29:35.911  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : Candle stick matches trades at 1638152880000
2021-11-29 02:29:35.911  INFO 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : expected: 27302549, CandleStick(timestamp=1638152940000, openPrice=57503.71, closePrice=57504.16, highPrice=57529.86, lowPrice=57491.24, volume=31.30423)
2021-11-29 02:29:35.911 ERROR 1087027 --- [nPool-worker-51] c.e.c.s.CryptoReconciliationImpl         : Candle stick doesn't match trades at 1638152940000
```

# Troubleshooting
## Timeout
Run the java program with arguments:
```bash
-Dhttp.proxyHost=${PROXY_HOST}
-Dhttps.proxyHost=${PROXY_HOST}
-Dhttp.proxyPort=${PROXY_PORT}
-Dhttps.proxyPort=${PROXY_PORT}
-Djava.net.useSystemProxies=true
```
