package com.da.app.controller.admin;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class FileDownloadController {

    @GetMapping("/downloadReport")
    public ResponseEntity<FileSystemResource> downloadReport(@RequestParam String reportPath) {
        File file = new File(reportPath);

        if (!file.exists()) {
            return ResponseEntity.status(404).body(null);
        }

        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .body(resource);
    }
}
