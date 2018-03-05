package controllers;

import components.FileLoader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class Controller {

//    @Autowired
    private FileLoader fileLoader;

    public Controller(FileLoader fileLoader){
        this.fileLoader = fileLoader;
    }

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("number") String number){
        return fileLoader.uploadFile(file,number);
    }


    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> serveFile(@RequestParam(value = "name") String docName) {
        return fileLoader.downloadFile(docName);
    }

    @RequestMapping(value = "/docs", method = RequestMethod.GET)
    public List<String> getDocList(){
        return fileLoader.getDocuments();
    }
}
