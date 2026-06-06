# City Drive Admin — Spring Boot Backend

Backend и веб-админка для мобильного приложения **CityDrive** (Flutter).

## Быстрый запуск

```bash
cd /home/adil/Downloads/city_drive_admin
./run.sh
```

Или вручную:

```bash
mvn spring-boot:run
```

## Открыть в браузере

| Страница | URL |
|----------|-----|
| Главная | http://localhost:8080/ |
| Вход | http://localhost:8080/login |
| Админ-панель | http://localhost:8080/admin |
| Проверка работы | http://localhost:8080/health |
| H2 Console | http://localhost:8080/h2-console |

**Логин админа:** `+77000000000` / `admin123`

## Открыть в IntelliJ IDEA

1. **File → Open** → выберите папку `city_drive_admin` (где лежит `pom.xml`)
2. IDEA спросит «Import Maven project» → нажмите **Trust Project** / **Load Maven Project**
3. Дождитесь скачивания зависимостей
4. Запуск: откройте `CityDriveAdminApplication.java` → зелёная стрелка **Run**

## API для Flutter

Базовый URL: `http://<IP-ноутбука>:8080/api`

```
POST /api/auth/login     — { "phone", "password" }
GET  /api/marks          — список (JWT)
GET  /api/marks/pending  — новые для контролёра
POST /api/marks          — создать отметку
PATCH /api/marks/{id}/status — смена статуса
```

## Учётные данные (seed)

| Роль | Телефон | Пароль |
|------|---------|--------|
| Админ | +77000000000 | admin123 |
| Житель | +77001111111 | resident1 |
| Контролёр | +77002222222 | controller1 |

## Связь с Flutter на другом ноуте/телефоне

1. Запустите backend на ноуте с Java
2. Узнайте IP: `hostname -I`
3. В Flutter укажите: `http://192.168.x.x:8080/api`
4. Оба устройства должны быть в одной Wi‑Fi сети

## Требования

- Java 17+ (у вас Java 21 — подходит)
- Maven 3.8+

## Сборка JAR

```bash
mvn clean package -DskipTests
java -jar target/city-drive-admin-1.0.0.jar
```
