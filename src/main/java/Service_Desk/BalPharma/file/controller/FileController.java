package Service_Desk.BalPharma.file.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
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

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String orig = file.getOriginalFilename();
        String ext = "";
        if (orig != null && orig.contains(".")) ext = orig.substring(orig.lastIndexOf('.'));
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        return ResponseEntity.ok(Map.of(
                "url", "/files/" + filename,
                "name", orig != null ? orig : filename
        ));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> download(@PathVariable String filename) throws IOException {

        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path target = root.resolve(filename).normalize();

        if (!target.startsWith(root)) return ResponseEntity.status(403).build();
        if (!Files.exists(target) || !Files.isReadable(target))
            return ResponseEntity.notFound().build();

        Resource resource = new UrlResource(target.toUri());

        MediaType mediaType = mediaTypeFor(filename);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    private static MediaType mediaTypeFor(String filename) {
        String name = filename.toLowerCase();
        int dot = name.lastIndexOf('.');
        String ext = dot >= 0 ? name.substring(dot + 1) : "";

        return switch (ext) {
            case "png"             -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg"     -> MediaType.IMAGE_JPEG;
            case "gif"             -> MediaType.IMAGE_GIF;
            case "webp"            -> MediaType.parseMediaType("image/webp");
            case "bmp"             -> MediaType.parseMediaType("image/bmp");
            case "svg"             -> MediaType.parseMediaType("image/svg+xml");
            case "ico"             -> MediaType.parseMediaType("image/x-icon");

            case "pdf"             -> MediaType.APPLICATION_PDF;
            case "txt", "log", "md"-> MediaType.TEXT_PLAIN;
            case "csv"             -> MediaType.parseMediaType("text/csv");
            case "json"            -> MediaType.APPLICATION_JSON;
            case "xml"             -> MediaType.APPLICATION_XML;
            case "html", "htm"     -> MediaType.TEXT_HTML;

            case "doc"             -> MediaType.parseMediaType("application/msword");
            case "docx"            -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "xls"             -> MediaType.parseMediaType("application/vnd.ms-excel");
            case "xlsx"            -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "ppt"             -> MediaType.parseMediaType("application/vnd.ms-powerpoint");
            case "pptx"            -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation");

            // media
            case "mp4"             -> MediaType.parseMediaType("video/mp4");
            case "webm"            -> MediaType.parseMediaType("video/webm");
            case "mov"             -> MediaType.parseMediaType("video/quicktime");
            case "mp3"             -> MediaType.parseMediaType("audio/mpeg");
            case "wav"             -> MediaType.parseMediaType("audio/wav");
            case "ogg"             -> MediaType.parseMediaType("audio/ogg");
            case "m4a"             -> MediaType.parseMediaType("audio/mp4");

            case "zip"             -> MediaType.parseMediaType("application/zip");

            default                -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}