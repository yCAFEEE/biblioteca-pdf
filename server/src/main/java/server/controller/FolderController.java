package server.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
            String folderName = body.get("name").trim();
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

    @DeleteMapping("/folders/{foldername:.+}")
    public ResponseEntity<?> deleteFolder(@PathVariable String foldername){
        try{
            Path targetPath = pdfsDirPath.resolve(foldername).normalize();
            if(!targetPath.startsWith(pdfsDirPath)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Diretório inválido"));
            }

            if(targetPath.toFile().list((dir, folder) -> folder.toLowerCase().endsWith(".pdf")).length > 0){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "A pasta " + foldername + " não está vazia"));
            }

            boolean deleted = Files.deleteIfExists(targetPath);
            if(deleted){
                return ResponseEntity.ok(Map.of("ok", "A pasta " + foldername + " foi deletado"));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pasta não foi encontrada"));
            }
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao deletar pasta"));
        }
    }

    @PutMapping("/folders/{foldername:.+}")
    public ResponseEntity<?> renameFolder(@PathVariable String foldername, @RequestBody Map<String, String> body){
        try{
            String newName = body.get("newName");

            Path sourcePath = pdfsDirPath.resolve(foldername).normalize();
            Path targetPath = pdfsDirPath.resolve(newName).normalize();
            if(!sourcePath.startsWith(pdfsDirPath) || !targetPath.startsWith(pdfsDirPath)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Diretório inválido"));
            }

            Files.move(sourcePath, targetPath);

            return ResponseEntity.ok(Map.of("message", "Pasta renomeada"));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao renomear pasta"));
        }
    }
}
