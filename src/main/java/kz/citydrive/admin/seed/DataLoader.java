package kz.citydrive.admin.seed;

import kz.citydrive.admin.domain.Company;
import kz.citydrive.admin.domain.CompanyStatus;
import kz.citydrive.admin.domain.LegalDocument;
import kz.citydrive.admin.domain.City;
import kz.citydrive.admin.domain.MarkStatus;
import kz.citydrive.admin.domain.News;
import kz.citydrive.admin.domain.RoadMark;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.domain.UserRole;
import kz.citydrive.admin.repository.CompanyRepository;
import kz.citydrive.admin.repository.LegalDocumentRepository;
import kz.citydrive.admin.repository.CityRepository;
import kz.citydrive.admin.repository.NewsRepository;
import kz.citydrive.admin.repository.RoadMarkRepository;
import kz.citydrive.admin.repository.UserRepository;
import kz.citydrive.admin.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Order(2)
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoadMarkRepository roadMarkRepository;
    private final NewsRepository newsRepository;
    private final LegalDocumentRepository legalDocumentRepository;
    private final CityRepository cityRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;

    public DataLoader(
            UserRepository userRepository,
            RoadMarkRepository roadMarkRepository,
            NewsRepository newsRepository,
            LegalDocumentRepository legalDocumentRepository,
            CityRepository cityRepository,
            CompanyRepository companyRepository,
            UserService userService) {
        this.userRepository = userRepository;
        this.roadMarkRepository = roadMarkRepository;
        this.newsRepository = newsRepository;
        this.legalDocumentRepository = legalDocumentRepository;
        this.cityRepository = cityRepository;
        this.companyRepository = companyRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsersAndMarks();
        }
        if (newsRepository.count() == 0) {
            seedNews();
        }
        if (legalDocumentRepository.count() == 0) {
            seedDocuments();
        }
        if (cityRepository.count() == 0) {
            seedCities();
        }
        approveLegacyUsers();
    }

    private void seedApprovedController(User controller, String companyName, String bin) {
        Company company = new Company();
        company.setUserId(controller.getId());
        company.setName(companyName);
        company.setBin(bin);
        company.setLegalAddress("г. Алматы, ул. Примерная 1");
        company.setFoundedYear(2015);
        company.setStatus(CompanyStatus.APPROVED);
        company.setSubmittedAt(Instant.now());
        companyRepository.save(company);

        controller.setApproved(true);
        userRepository.save(controller);
    }

    private void approveLegacyUsers() {
        for (var user : userRepository.findAll()) {
            if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.RESIDENT) {
                if (!user.isApproved()) {
                    user.setApproved(true);
                    userRepository.save(user);
                }
            }
        }
    }

    private void seedUsersAndMarks() {

        var admin = userService.createUser("Админ Системы", "+77000000000", "admin123", UserRole.ADMIN);
        var resident1 = userService.createUser("Айгуль Нурланова", "+77001111111", "resident1", UserRole.RESIDENT);
        var resident2 = userService.createUser("Ерлан Касымов", "+77003333333", "resident2", UserRole.RESIDENT);
        resident1.setCityId(1);
        resident1.setBirthDate("1992-03-20");
        resident1.setLang("ru");
        userRepository.save(resident1);
        resident2.setCityId(1);
        resident2.setLang("kk");
        userRepository.save(resident2);
        var controller1 = userService.createUser("Марат Оспанов", "+77002222222", "controller1", UserRole.CONTROLLER);
        var controller2 = userService.createUser("Динара Сейтова", "+77004444444", "controller2", UserRole.CONTROLLER);
        seedApprovedController(controller1, "ТОО Road Control", "770022222001");
        seedApprovedController(controller2, "ТОО City Monitor", "770044444001");

        List<RoadMarkSeed> seeds = List.of(
                seed(resident1.getId(), null, "Яма на проспекте", "Глубокая яма", "пр. Абая 120", 43.238, 76.945, "pothole", "high", MarkStatus.NEW, 0),
                seed(resident1.getId(), null, "Нет разметки", "Стерлась разметка", "ул. Толе би 45", 43.241, 76.912, "marking", "medium", MarkStatus.PENDING, 1),
                seed(resident2.getId(), controller1.getId(), "Светофор не работает", "Перекрёсток без сигнала", "ул. Сатпаева 10", 43.256, 76.928, "traffic_light", "critical", MarkStatus.CONFIRMED, 2),
                seed(resident2.getId(), controller1.getId(), "Провал люка", "Люк провалился", "ул. Достык 88", 43.229, 76.951, "manhole", "high", MarkStatus.IN_PROGRESS, 3),
                seed(resident1.getId(), controller2.getId(), "Обледенение", "Скользко на пешеходном", "пр. Аль-Фараби 25", 43.218, 76.890, "ice", "medium", MarkStatus.IN_PROGRESS, 4),
                seed(resident2.getId(), null, "Мусор на дороге", "Строительный мусор", "ул. Жандосова 5", 43.245, 76.870, "debris", "low", MarkStatus.REJECTED, 5),
                seed(resident1.getId(), controller2.getId(), "Трещина асфальта", "Длинная трещина", "ул. Байтурсынова 30", 43.252, 76.935, "crack", "medium", MarkStatus.FIXED, 10),
                seed(resident2.getId(), null, "Затопление", "Лужа после дождя", "ул. Курмангазы 12", 43.262, 76.905, "flooding", "high", MarkStatus.NEW, 0),
                seed(resident1.getId(), null, "Отсутствует знак", "Нет знака уступи", "пр. Республики 50", 43.235, 76.960, "sign", "medium", MarkStatus.PENDING, 1),
                seed(resident2.getId(), controller1.getId(), "Бордюр разрушен", "Сломан бордюр", "ул. Ауэзова 70", 43.248, 76.942, "curb", "low", MarkStatus.CONFIRMED, 2),
                seed(resident1.getId(), controller2.getId(), "Освещение", "Фонарь не горит", "ул. Шаляпина 3", 43.227, 76.918, "lighting", "medium", MarkStatus.FIXED, 8),
                seed(resident2.getId(), null, "Выбоина у школы", "Опасно для детей", "ул. Карасай батыра 15", 43.240, 76.898, "pothole", "critical", MarkStatus.NEW, 0));

        for (RoadMarkSeed s : seeds) {
            RoadMark mark = new RoadMark();
            mark.setAuthorUserId(s.authorUserId());
            mark.setAssignedControllerId(s.controllerId());
            mark.setTitle(s.title());
            mark.setDescription(s.description());
            mark.setAddress(s.address());
            mark.setLat(s.lat());
            mark.setLng(s.lng());
            mark.setType(s.type());
            mark.setSeverity(s.severity());
            mark.setStatus(s.status());
            mark.setReportedDate(Instant.now().minus(s.daysAgo(), ChronoUnit.DAYS));
            mark.setImagesJson("[\"https://picsum.photos/seed/" + s.title().hashCode() + "/400/300\"]");
            mark.setAuthor(s.authorUserId().equals(resident1.getId()) ? resident1.getFullName() : resident2.getFullName());
            mark.setLikes((int) (Math.random() * 20));
            mark.setCommentsCount((int) (Math.random() * 8));
            mark.setAdminNote(s.status() == MarkStatus.REJECTED ? "Дубликат заявки" : null);
            roadMarkRepository.save(mark);
        }
    }

    private static RoadMarkSeed seed(
            Long authorId,
            Long controllerId,
            String title,
            String description,
            String address,
            double lat,
            double lng,
            String type,
            String severity,
            MarkStatus status,
            int daysAgo) {
        return new RoadMarkSeed(authorId, controllerId, title, description, address, lat, lng, type, severity, status, daysAgo);
    }

    private record RoadMarkSeed(
            Long authorUserId,
            Long controllerId,
            String title,
            String description,
            String address,
            double lat,
            double lng,
            String type,
            String severity,
            MarkStatus status,
            int daysAgo) {}

    private void seedNews() {
        record NewsSeed(String title, String description, String imageUrl, int daysAgo) {}

        List<NewsSeed> items = List.of(
                new NewsSeed(
                        "Ремонт улицы Абая",
                        "С 15 по 30 ноября перекрыта правая полоса. Объезд через ул. Толе би.",
                        "https://picsum.photos/seed/abay/800/400",
                        2),
                new NewsSeed(
                        "Новый светофор на проспекте",
                        "На перекрёстке пр. Сатпаева и ул. Байтурсынова установлен умный светофор с адаптивным режимом.",
                        "https://picsum.photos/seed/trafficlight/800/400",
                        4),
                new NewsSeed(
                        "Зимняя уборка дорог",
                        "Коммунальные службы переходят на зимний график работы. Посыпка антигололёдными материалами начнётся при понижении температуры ниже -3°C.",
                        null,
                        7));

        for (NewsSeed s : items) {
            News news = new News();
            news.setTitle(s.title());
            news.setDescription(s.description());
            news.setImageUrl(s.imageUrl());
            news.setPublishedAt(Instant.now().minus(s.daysAgo(), ChronoUnit.DAYS));
            news.setPublished(true);
            newsRepository.save(news);
        }
    }

    private void seedDocuments() {
        Instant now = Instant.now();

        LegalDocument terms = new LegalDocument();
        terms.setTitleKk("Пайдаланушы келісімі");
        terms.setTitleRu("Пользовательское соглашение");
        terms.setTitleEn("Terms of Service");
        terms.setContentKk("https://citydrive.kz/docs/terms-kk.html");
        terms.setContentRu("https://citydrive.kz/docs/terms-ru.html");
        terms.setContentEn("https://citydrive.kz/docs/terms-en.html");
        terms.setType("url");
        terms.setSortOrder(1);
        terms.setActive(true);
        terms.setCreatedAt(now);
        terms.setUpdatedAt(now);
        legalDocumentRepository.save(terms);

        LegalDocument privacy = new LegalDocument();
        privacy.setTitleKk("Құпиялылық саясаты");
        privacy.setTitleRu("Политика конфиденциальности");
        privacy.setTitleEn("Privacy Policy");
        privacy.setContentKk("<p>City Drive қосымшасы сіздің деректеріңізді қорғауға міндеттенеді.</p>");
        privacy.setContentRu("<p>Приложение City Drive обязуется защищать ваши персональные данные.</p>");
        privacy.setContentEn("<p>City Drive app is committed to protecting your personal data.</p>");
        privacy.setType("html");
        privacy.setSortOrder(2);
        privacy.setActive(true);
        privacy.setCreatedAt(now);
        privacy.setUpdatedAt(now);
        legalDocumentRepository.save(privacy);
    }

    private void seedCities() {
        saveCity("Алматы", 1);
        saveCity("Астана", 2);
        saveCity("Шымкент", 3);
    }

    private void saveCity(String name, int sortOrder) {
        City city = new City();
        city.setName(name);
        city.setSortOrder(sortOrder);
        city.setActive(true);
        cityRepository.save(city);
    }
}
