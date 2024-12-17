package vn.hoidanit.jobhunter.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri); // convert uri -> path will avoid variable baseURI(file://)
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + tmpDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");
        }

    }

    public String store(String folder, MultipartFile file) throws URISyntaxException, IOException {
        // create unique fileName
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        URI uri = new URI(folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    // store file: https://spring.io/guides/gs/uploading-files
    // https://stackoverflow.com/questions/53251949/how-to-create-a-permanent-directory-for-my-files-with-spring-boot/53252034#53252034
}
