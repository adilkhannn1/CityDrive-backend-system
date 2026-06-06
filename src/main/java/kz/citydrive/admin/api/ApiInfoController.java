package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.SupportInfoDto;
import kz.citydrive.admin.service.SupportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiInfoController {

    @Value("${server.port:8080}")
    private int serverPort;

    private final SupportService supportService;

    public ApiInfoController(SupportService supportService) {
        this.supportService = supportService;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        List<String> ips = detectLanIps();
        String primaryIp = ips.isEmpty() ? "localhost" : ips.get(0);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", "city-drive-admin");
        body.put("status", "UP");
        body.put("port", serverPort);
        body.put("lan_ips", ips);
        body.put("flutter_base_url", "http://" + primaryIp + ":" + serverPort + "/api");
        body.put("health_url", "http://" + primaryIp + ":" + serverPort + "/health");
        body.put("login_url", "http://" + primaryIp + ":" + serverPort + "/api/login");
        body.put("marks_url", "http://" + primaryIp + ":" + serverPort + "/api/marks");
        body.put("test_user", Map.of(
                "phone", "+77001111111",
                "password", "resident1",
                "role", "RESIDENT"));
        body.put("support", toSupportMap(supportService.getSupportInfo()));
        return body;
    }

    private Map<String, String> toSupportMap(SupportInfoDto support) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("whatsapp_phone", support.getWhatsappPhone());
        map.put("whatsapp_message", support.getWhatsappMessage());
        map.put("email", support.getEmail());
        map.put("phone", support.getPhone());
        return map;
    }

    private static List<String> detectLanIps() {
        List<String> ips = new ArrayList<>();
        try {
            var interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }
                var addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        ips.add(address.getHostAddress());
                    }
                }
            }
        } catch (Exception ignored) {
            // ignore
        }
        return ips;
    }
}
