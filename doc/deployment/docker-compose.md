# Docker Compose Deployment

## Deploy localy

A demo docker-compose manifest is available in the `docker-compose/local` folder. It will launch a kafka broker, a zookeeper and all the micro services from the solution.

```
$ cd docker-compose/local
$ docker-compose up -d
```

| Service | Url |
|---|---|
| UI | http://localhost:8080 |
| Rate | http://localhost:8081 |
| Transaction | http://localhost:8082 |
| Wallet | http://localhost:8083 |