# Web-сервис вычислений 

Реализовано с помощью Spring WebFlux и Kafka.

## Сборка и упаковка

Для сборки приложения выполнить:

```
mvnw package
```

Для упаковки в Docker контейнер:

```
docker build -t hse-webflux .
```

## Настройка и развертывание

Необходимо заполнить корректное значение kafka.url (либо в application.properties либо через параметра запуска JVM).

Для работы необходимы созданные очереди в Kafka. Для создания можно воспользоваться командами:

```
kafka-topics.sh --create --zookeeper zookeeper:2181 --topic calcReq --partitions 1 --replication-factor 1
kafka-topics.sh --create --zookeeper zookeeper:2181 --topic calcRes --partitions 1 --replication-factor 1
```

где **zookeeper:2181** это адрес кластера Zookeeper.

## Запуск через Docker

Для запуска упакованного в контейнер приложения

```
docker run --name hse-webflux -p 8080:8080 -e kafka.url=URL_kafka -d hse-webflux:latest
```