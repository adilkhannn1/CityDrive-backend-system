# City Drive Admin (Spring Boot)

Отдельный backend + веб-админка для мобильного приложения **city_drive** (Flutter).

```
flutter_projects/
├── city_drive/          ← Flutter app
└── city_drive_admin/    ← этот проект (Java Spring Boot)
```

## Запуск

```bash
cd city_drive_admin
./mvnw spring-boot:run
```

Или с установленным Maven:

```bash
mvn spring-boot:run
```

## URL

| Что | Адрес |
|-----|--------|
| Админ-панель (веб) | http://localhost:8080/admin |
| Вход админа | http://localhost:8080/login |
| REST API (Flutter) | http://localhost:8080/api/... |
| H2 Console | http://localhost:8080/h2-console (JDBC: `jdbc:h2:file:./data/citydrive`) |

## Учётные данные (seed)

| Роль | Телефон | Пароль |
|------|---------|--------|
| Админ | `+77000000000` | `admin123` |
| Житель | `+77001111111` | `resident1` |
| Контролёр | `+77002222222` | `controller1` |

## API для Flutter

Базовый URL: `http://<IP-Mac>:8080/api`

- `POST /api/auth/login` — `{ "phone", "password" }`
- `GET /api/marks` — список отметок (JWT)
- `GET /api/marks/pending` — новые для контролёра
- `POST /api/marks` — создать отметку
- `PATCH /api/marks/{id}/status` — смена статуса

Статусы: `new`, `pending`, `confirmed`, `rejected`, `in_progress`, `fixed`

## Связь с Flutter

В `city_drive` укажи `baseUrl` на этот сервер (не `localhost` с телефона — IP компьютера в Wi‑Fi).
