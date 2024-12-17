package vn.hoidanit.jobhunter.controller;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.service.FileService;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("folder") String folder) throws URISyntaxException {
        // validate


        // create a directory if not exist
        this.fileService.createDirectory(baseURI + folder);

        // store file

        return file.getOriginalFilename();
    }
}
