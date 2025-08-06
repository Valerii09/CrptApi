# WalletApp

REST-сервис кошелька с балансом и операциями DEPOSIT / WITHDRAW.

## Стек
- Java 17, Spring Boot 3
- PostgreSQL + Liquibase
- Docker, Docker Compose
- Макроустойчивость при высоком трафике (1000 RPS на один кошелек)

---

## ⚙️ Как запустить

1. Собрать приложение:
```bash
./mvnw clean package -DskipTests
