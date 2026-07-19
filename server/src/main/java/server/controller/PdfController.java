package server.controller;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class PdfController{
    private final String pdfsDir = "../client/public/pdfDir";
    private final Path pdfsDirPath = Path.of(pdfsDir);

    @PostConstruct
    public void init(){
        try{
            if(!Files.exists(pdfsDirPath)){
                Files.createDirectory(pdfsDirPath);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Erro ao criar a pasta pdfDir/");
        }
    }

    @GetMapping("/pdfs")
    public ResponseEntity<?> listPdfs(){
        try{
            File folder = new File(pdfsDir);
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            List<Map<String, String>> list = new ArrayList<>();

            if(files != null){
                for(File file : files){
                    String name = file.getName();
                    String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
                    int pagesCount = 0;

                    try(PDDocument fileDocument = Loader.loadPDF(file);){
                        pagesCount = fileDocument.getNumberOfPages();
                    }catch(Exception e){
                        System.err.println("Erro ao ler a quantidade de paginas do arquivo: " + name);
                    }

                    Map<String, String> pdfInfo = new HashMap<>();
                    pdfInfo.put("name", name);
                    pdfInfo.put("url", "/pdfs-files/" + encodedName);
                    pdfInfo.put("pages", String.valueOf(pagesCount));

                    list.add(pdfInfo);
                }
            }
            return ResponseEntity.ok(list);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao listar os arquivos PDFs"));
        }
    }
    
    @PostMapping("/pdfs")
    public ResponseEntity<?> uploadPdf(@RequestParam MultipartFile file){
        try{
            String name = file.getOriginalFilename();
            if(name == null || name.isBlank()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Nome do arquivo não pode ser vazio"));
            }

            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
    
            Files.copy(file.getInputStream(), pdfsDirPath.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            int pagesCount = 0;
            try(PDDocument fileDocument = Loader.loadPDF(pdfsDirPath.resolve(name).toFile())){
                pagesCount = fileDocument.getNumberOfPages();
            }
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("name", name);
            fileInfo.put("url", "/pdfs-files/" + encodedName);
            fileInfo.put("pages", String.valueOf(pagesCount));

            return ResponseEntity.ok(fileInfo);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao enviar o arquivo"));
        }
    }

    @DeleteMapping("/pdfs/{filename:.+}")
    public ResponseEntity<?> deletePdf(@PathVariable String filename){
        try{
            Path targetPath = pdfsDirPath.resolve(filename).normalize();
            if(!targetPath.startsWith(pdfsDirPath)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Diretório inválido"));
            }

            boolean deleted = Files.deleteIfExists(targetPath);
            if(deleted){
                return ResponseEntity.ok(Map.of("message", "PDF " + filename + " foi deletado"));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Arquivo não encontrado"));
            }
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao excluir o arquivo"));
        }
    }

    @PutMapping("/pdfs/{filename:.+}")
    public ResponseEntity<?> renamePdf(@PathVariable String filename, @RequestBody Map<String,String> body) {
        try{
            String newName = body.get("newName");
            if(!newName.toLowerCase().endsWith(".pdf")){
                newName += ".pdf";
            }

            Path sourcePath = pdfsDirPath.resolve(filename).normalize();
            Path targetPath = pdfsDirPath.resolve(newName).normalize();
            if(!sourcePath.startsWith(pdfsDirPath) || !targetPath.startsWith(pdfsDirPath)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Diretório inválido"));
            }

            Files.move(sourcePath, targetPath);

            return ResponseEntity.ok(Map.of("message", "Arquivo renomeado"));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao renomear arquivo"));
        }
    }
}