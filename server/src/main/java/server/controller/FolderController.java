package server.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class FolderController{
    private final String pdfsDir = "../client/public/pdfDir";
    private final Path pdfsDirPath = Path.of(pdfsDir);

    @GetMapping("/folders")
    public ResponseEntity<?> listFolders(){
        try{
            File pdfsDirFolder = new File(pdfsDir);
            File[] folders = pdfsDirFolder.listFiles(File::isDirectory);
            List<Map<String, String>> list = new ArrayList<>();

            if(folders != null){
                for(File folder : folders){
                    String name = folder.getName();
                    int filesCount = folder.listFiles((dir, fileName) -> fileName.toLowerCase().endsWith(".pdf")).length;

                    Map<String, String> folderInfo = new HashMap<>();
                    folderInfo.put("name", name);
                    folderInfo.put("files", String.valueOf(filesCount));

                    list.add(folderInfo);
                }
            }

            return ResponseEntity.ok(list);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao listar pastas"));
        }
    }

    @PostMapping("/folders")
    public ResponseEntity<?> createFolder(@RequestParam Map<String, String> body){
        try{
            String folderName = body.get("name");
            Path folderPath = Path.of(pdfsDir + "/" + folderName);

            Files.createDirectory(folderPath);

            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("name", folderName);
            fileInfo.put("files", "0");

            return ResponseEntity.ok(fileInfo);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao criar pasta"));
        }
    }
}
