# Учебный проект: три микросервиса

Проект повторяет форму и список сообщений из `react_plus_sb` и показывает,
как данные проходят через три независимо запускаемых приложения:

```text
React 1 (5173) -> Spring Boot 1 (8080) -> MySQL: demo_one
                                      \-> outbox -> Spring Boot 2 (8081) -> MySQL: demo_two
React 3 (5174) <- Spring Boot 3 (8082) <- SQLite: service-three.db
                                      \- периодически читает Spring Boot 2
```

Первый сервис немедленно сохраняет сообщение в своей MySQL и в той же
транзакции ставит его в outbox. Фоновый отправитель повторяет доставку в сервис
2, пока тот не подтвердит запись. Сервис 2 использует UUID события, поэтому
повторная доставка не создаёт дубликаты. Сервис 3 раз в 5 секунд получает
сообщения сервиса 2 и сохраняет новые строки в SQLite с временем первого
получения в часовом поясе `Europe/Warsaw`. React 3 показывает эти сообщения,
локальную фотографию собаки и текущие часы Варшавы.

Docker Compose не используется. MySQL запускается как локальная служба.

## Что установить

- Java 17 или новее
- Maven 3.8+
- Node.js 20+ и npm
- MySQL Server 8+

Все команды ниже рассчитаны на Linux и запускаются из корня проекта
`/home/karim/react_ex/3_react_spring_boot`.

## Подготовка MySQL (один раз)

Установите MySQL Server, если он ещё не установлен, и запустите системную
службу:

```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl enable --now mysql
```

Создайте две отдельные базы и локального пользователя для демо:

```bash
sudo mysql
```

В приглашении MySQL выполните:

```sql
CREATE DATABASE demo_one CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE demo_two CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'demo_user'@'localhost' IDENTIFIED BY 'demo_password';
GRANT ALL PRIVILEGES ON demo_one.* TO 'demo_user'@'localhost';
GRANT ALL PRIVILEGES ON demo_two.* TO 'demo_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

`demo_password` — только пароль для локального учебного стенда. Для другого
пароля задайте переменную `DB_PASSWORD` перед запуском Spring Boot-сервисов.
При необходимости также можно задать `DB_USERNAME` и `DB_HOST`.

## Первый запуск

Откройте пять терминалов в корне проекта. Установка frontend-зависимостей
нужна только при первом запуске.

**Терминал 1 — Spring Boot 2, приёмник и MySQL `demo_two`:**

```bash
cd /home/karim/react_ex/3_react_spring_boot/service-2/backend
mvn spring-boot:run
```

**Терминал 2 — Spring Boot 1, оригинальное приложение и MySQL `demo_one`:**

```bash
cd /home/karim/react_ex/3_react_spring_boot/service-1/backend
mvn spring-boot:run
```

Сервис 1 отправляет накопленные события в сервис 2 автоматически. Порт
сервиса 2 по умолчанию `8081`; изменить его можно через `SERVER_PORT`.

**Терминал 3 — Spring Boot 3, синхронизация с сервисом 2 и SQLite:**

```bash
cd /home/karim/react_ex/3_react_spring_boot/service-3/backend
mvn spring-boot:run
```

Файл базы SQLite `service-three.db` создаётся в папке backend автоматически.
Если сервис 2 пока недоступен, сервис 3 продолжит запуск и повторит чтение
через 5 секунд.

**Терминал 4 — React-интерфейс сервиса 1:**

```bash
cd /home/karim/react_ex/3_react_spring_boot/service-1/frontend
npm install
npm run dev
```

**Терминал 5 — React-интерфейс сервиса 3:**

```bash
cd /home/karim/react_ex/3_react_spring_boot/service-3/frontend
npm install
npm run dev
```

Откройте:

- приложение 1: <http://localhost:5173>
- приложение 3: <http://localhost:5174>

Отправьте сообщение в приложении 1. Оно появится там сразу, попадёт в MySQL
сервиса 2 фоновым отправителем и отобразится в приложении 3 после очередной
синхронизации (не более чем примерно через 5 секунд).

## Порты и настройки

| Компонент | Порт / адрес по умолчанию |
| --- | --- |
| React сервиса 1 | 5173 |
| Spring Boot сервиса 1 | 8080 |
| Spring Boot сервиса 2 | 8081 |
| Spring Boot сервиса 3 | 8082 |
| React сервиса 3 | 5174 |
| MySQL | `localhost:3306` |

Для MySQL можно переопределить `DB_HOST`, `DB_PORT`, `DB_USERNAME` и
`DB_PASSWORD`. Адрес сервиса 2 для сервиса 1 задаётся через
`SERVICE2_URL` (по умолчанию `http://localhost:8081`), адрес сервиса 2 для
сервиса 3 — через `ARCHIVE_URL` (тот же адрес по умолчанию). Интервал
синхронизации сервиса 3 можно изменить через `SYNC_INTERVAL_MS`.

## Сколько места занимает

Ориентировочная оценка для Linux после установки зависимостей и первой сборки:

- исходники проекта и фотография: менее 2 МБ;
- Maven-кэш общих Java-зависимостей: около 400–800 МБ;
- три собранных Spring Boot JAR и временные результаты сборки: около 150–250 МБ;
- два `node_modules` и production-сборки React: около 100–180 МБ;
- установленный MySQL Server с системными пакетами: ориентировочно 300–600 МБ;
- данные MySQL и SQLite для этого демо: обычно менее 20 МБ.

Итого с установленным MySQL, кэшем Maven и frontend-зависимостями планируйте
примерно **1–2 ГБ**. Размер зависит от уже имеющегося кэша Maven и состава
установленных системных пакетов. `node_modules`, Maven-кэш и MySQL не входят
в исходники проекта; фактический размер можно проверить командами `du -sh`.
В текущей собранной копии каталоги проекта занимают около **210 МБ** без
установки MySQL и внешнего Maven-кэша.

## Проверка API

```bash
curl http://localhost:8080/api/greetings
curl http://localhost:8081/api/greetings
curl http://localhost:8082/api/greetings
```

Проверка записи через первый сервис:

```bash
curl -X POST http://localhost:8080/api/greetings \
  -H 'Content-Type: application/json' \
  -d '{"name":"Анна","message":"Привет из трёх микросервисов!"}'
```
