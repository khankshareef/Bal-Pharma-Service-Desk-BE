package Service_Desk.BalPharma.template.bootstrap;

import Service_Desk.BalPharma.template.entity.InvestigationTemplateEntity;
import Service_Desk.BalPharma.template.repository.InvestigationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class TemplateSeeder implements CommandLineRunner {

    private final InvestigationTemplateRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        repo.saveAll(List.of(
                newTemplate("Hardware Check",     "tool",    "gray",   "INVESTIGATION", 1,
                        "🔧 Hardware Investigation:\n1. Check physical connections\n2. Verify power supply\n3. Test with known good device\n4. Check device drivers\n5. Run diagnostics"),
                newTemplate("Network Diagnostic", "globe",   "blue",   "INVESTIGATION", 2,
                        "🌐 Network Diagnostic:\n1. Check network cable/connection\n2. Ping gateway\n3. Verify DNS settings\n4. Check firewall rules\n5. Run traceroute"),
                newTemplate("Software Debug",     "monitor", "cyan",   "INVESTIGATION", 3,
                        "💻 Software Debug:\n1. Check error logs\n2. Verify application version\n3. Test in safe mode\n4. Check dependencies\n5. Reinstall if needed"),
                newTemplate("Access Review",      "key",     "yellow", "INVESTIGATION", 4,
                        "🔑 Access Review:\n1. Verify user permissions\n2. Check group membership\n3. Review access logs\n4. Validate AD/LDAP sync\n5. Grant/revoke access"),

                newTemplate("Acknowledge",        "message", "purple", "RESPONSE", 1,
                        "📨 Ticket acknowledged. We are looking into this issue and will get back to you shortly."),
                newTemplate("Investigating",      "search",  "blue",   "RESPONSE", 2,
                        "🔍 We are currently investigating this issue. Please bear with us while we analyze the problem."),
                newTemplate("Resolved",           "check",   "green",  "RESPONSE", 3,
                        "✅ The issue has been resolved. Please confirm if everything is working as expected."),
                newTemplate("More Info",          "help",    "red",    "RESPONSE", 4,
                        "❓ Could you please provide more details about the issue? When did it start? Any error messages?")
        ));
    }

    private InvestigationTemplateEntity newTemplate(
            String title, String icon, String color, String type, int order, String content) {
        InvestigationTemplateEntity e = new InvestigationTemplateEntity();
        e.setTitle(title);
        e.setIcon(icon);
        e.setColor(color);
        e.setType(type);
        e.setContent(content);
        e.setDisplayOrder(order);
        e.setActive(true);
        return e;
    }
}