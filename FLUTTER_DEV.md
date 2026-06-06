# Подключение Flutter к backend (City Drive)

## Для Flutter-разработчика

### 1. Оба ноутбука в одной Wi‑Fi сети

Backend-разработчик запускает сервер, затем открывает в браузере:

```
http://<IP-backend-ноута>:8080/api/info
```

Там будет `flutter_base_url` — его нужно вставить во Flutter.

### 2. Изменить baseUrl во Flutter

Файл: `lib/src/core/containers/repository_storage.dart`

```dart
baseUrl: 'http://10.201.122.134:8080/api',  // IP backend-ноута
```

Замените `10.201.122.134` на IP из `/api/info`.

### 3. Проверка связи (с ноута Flutter)

```bash
curl http://10.201.122.134:8080/health
curl http://10.201.122.134:8080/api/info
```

### 4. Тест логина

```bash
curl -X POST http://10.201.122.134:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"+77001111111","password":"resident1"}'
```

Ответ (snake_case):
```json
{
  "token": "eyJ...",
  "id": 2,
  "full_name": "Айгуль Нурланова",
  "phone": "+77001111111",
  "role": "RESIDENT"
}
```

### 5. API отметок на карте

```
GET  /api/marks          — все отметки (нужен JWT: Authorization: Bearer <token>)
GET  /api/marks/pending  — новые для контролёра
POST /api/marks          — создать отметку
PATCH /api/marks/{id}/status — сменить статус
```

### 6. Тестовые пользователи

| Роль | Телефон | Пароль |
|------|---------|--------|
| Житель | +77001111111 | resident1 |
| Контролёр | +77002222222 | controller1 |
| Админ | +77000000000 | admin123 |

### 7. Android эмулятор vs реальный телефон

| Устройство | baseUrl |
|------------|---------|
| Реальный телефон / другой ноут | `http://192.168.x.x:8080/api` |
| Android эмулятор | `http://10.0.2.2:8080/api` |
| iOS симулятор | `http://localhost:8080/api` |

### 8. Сейчас во Flutter

Карта использует **mock-данные** (`RoadProblemDTO.getMockData()`).
Чтобы подключить backend, нужно в `RoadProblemsProvider` загружать данные с `GET /api/marks`.

Поля ответа backend совпадают с `RoadProblemDTO`:
`id`, `title`, `description`, `address`, `latitude`, `longitude`, `type`, `severity`, `status`, `reported_date`, `images`, `author`, `likes`, `comments_count`

---

## Для backend-разработчика

Запуск:
```bash
cd city_drive_admin
mvn spring-boot:run
```

Узнать IP:
```bash
hostname -I
```

Проверить что сервер доступен из сети:
```bash
curl http://localhost:8080/api/info
```
