package components;


import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FileLoader {

    private File roodDir;

    public FileLoader() {
        this.roodDir = getRootDir();
    }

    /**
     * Загрузить файл на сервер
     * @param file файл
     * @param number номер документа
     * @return
     */
    public String uploadFile(MultipartFile file, String number){
        try {
            byte[] bytes = file.getBytes();

            File dir = new File(getRootDir().getAbsolutePath() +File.separator+ getCatalogName(number));

            if( !dir.exists() ){
                dir.mkdir();
            }

            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename()))
            );
            stream.write(bytes);
            stream.close();

            return number;
        } catch (Exception ex){
            return null;
        }
    }

    /**
     * Загрузить документ с сервера
     * @param docName
     * @return
     */
    public ResponseEntity<InputStreamResource> downloadFile(String docName){
        try {
            String filePath = getRootDir().getAbsolutePath() +File.separator+ docName;
            File dir = new File(filePath);

            // Ищем последний созданный файл в каталоге
            File downloadedFile = Arrays.stream(dir.listFiles()).filter(File::isFile).max((f1,f2)->{
                try {
                    Path p1 = f1.toPath();
                    Path p2 = f2.toPath();

                    BasicFileAttributes bfa1 = Files.readAttributes(p1, BasicFileAttributes.class);
                    BasicFileAttributes bfa2 = Files.readAttributes(p2, BasicFileAttributes.class);

                    return bfa1.creationTime().compareTo(bfa2.creationTime());
                } catch (Exception ex){
                    return 0;
                }
            }).get();

            InputStreamResource isr = new InputStreamResource(new FileInputStream(downloadedFile));
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + downloadedFile.getName() + "\"").body(isr);
        }
        catch (Exception ex){
            return null;
        }
    }

    /**
     * Получить список документов
     * @return
     */
    public List<String> getDocuments(){
        try {
            File dir = getRootDir();
            if(dir.listFiles().length>0) {
                return Arrays.stream(dir.listFiles()).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());
            }
            else{
                return new ArrayList<>();
            }
        } catch(Exception ex){
            return null;
        }
    }

    /**
     * Получить номер нового документа
     * @return
     */
    private String getCatalogName(String number){
        if(number.equals("new")) {

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");

            // ищем документ с максимальным номером
            Integer maxNumber = getDocuments().stream()
                    .map(o->Integer.valueOf(o.split(" от ")[0]))
                    .reduce(Integer::max).orElse(-1);

            return String.valueOf(maxNumber+1) +" от "+ format.format(date);
        }
        else {
            // по номеру документа ищем каталог
            return getDocuments().stream().filter(o->o.split(" от ")[0].equals(number)).collect(Collectors.toList()).get(0);
        }
    }

    /**
     * Каталог с документами
     * @return
     */
    private File getRootDir(){
        File rootDir = new File(System.getProperty("catalina.home") +File.separator+ "documents");
        if(!rootDir.exists()){
            rootDir.mkdir();
        }
        return rootDir;
    }
}
