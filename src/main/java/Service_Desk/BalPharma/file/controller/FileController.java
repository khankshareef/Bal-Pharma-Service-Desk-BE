package Service_Desk.BalPharma.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Empty file");

        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);

        String ext = "";
        String orig = file.getOriginalFilename();
        if (orig != null && orig.contains(".")) {
            ext = orig.substring(orig.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        return ResponseEntity.ok(Map.of(
                "url", "/files/" + filename,
                "name", orig != null ? orig : filename
        ));
    }

    @GetMapping("/{filename}")
    public ResponseEntity<?> download(@PathVariable String filename) throws IOException {
        Path target = Paths.get(uploadDir).resolve(filename);
        if (!Files.exists(target)) return ResponseEntity.notFound().build();

        byte[] bytes = Files.readAllBytes(target);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                .body(bytes);
    }
}