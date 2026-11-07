package server.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.imageio.ImageIO;

import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class FolderPdfController{
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

    @GetMapping("/folders/{folderName:.+}/pdfs")
    public ResponseEntity<?> listFolderPdfs(@PathVariable String folderName){
        try{
            File folder = new File(pdfsDir + "/" + folderName);
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            List<Map<String, String>> list = new ArrayList<>();

            if(files != null){
                for(File file : files){
                    String name = file.getName();
                    String encodedFolder = URLEncoder.encode(folderName, StandardCharsets.UTF_8).replace("+", "%20");
                    String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
                    int pagesCount = 0;

                    try(PDDocument fileDocument = Loader.loadPDF(file)){
                        pagesCount = fileDocument.getNumberOfPages();
                    }catch(Exception e){
                        System.err.println("Erro ao ler a quantidade de páginas do arquivo: " + name);
                    }

                    Map<String, String> fileInfo = new HashMap<>();
                    fileInfo.put("name", name);
                    fileInfo.put("pages", String.valueOf(pagesCount));
                    fileInfo.put("url", "folders/" + encodedFolder + "/pdfs/" + encodedName);
                    fileInfo.put("thumbnail", "http://localhost:8080/folders/" + encodedFolder + "/pdfs/" + encodedName + "/cover");
                    list.add(fileInfo);
                }
            }
            return ResponseEntity.ok(list);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao listar PDFs dentro da pasta"));
        }
    }

    @DeleteMapping("folders/{foldername:.+}/pdfs/{filename:.+}")
    public ResponseEntity<?> deleteFolderPdf(@PathVariable String foldername, @PathVariable String filename){
        try{
            Path targetPath = pdfsDirPath.resolve(foldername + "/" + filename).normalize();
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao deletar arquivo na pasta " + foldername));
        }
    }

    @PutMapping("folders/{foldername:.+}/pdfs/{filename:.+}")
    public ResponseEntity<?> renamePdf(@PathVariable String foldername, @PathVariable String filename, @RequestBody Map<String, String> body){
        try{
            String newName = body.get("newName");
            if(!newName.toLowerCase().endsWith(".pdf")){
                newName += ".pdf";
            }

            Path sourcePath = pdfsDirPath.resolve(foldername + "/" + filename).normalize();
            Path targetPath = pdfsDirPath.resolve(foldername + "/" + newName).normalize();
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

    @GetMapping(value = "folders/{foldername:.+}/pdfs/{filename:.+}/cover", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getFolderPdfCover(@PathVariable String foldername, @PathVariable String filename){
        try{
            Path targetPath = pdfsDirPath.resolve(foldername + "/" + filename).normalize();

            if(!targetPath.startsWith(pdfsDirPath) || !Files.exists(targetPath)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Diretório inválido"));
            }

            try(PDDocument document = Loader.loadPDF(targetPath.toFile())){
                if(document.getNumberOfPages() > 0){
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    BufferedImage img = pdfRenderer.renderImageWithDPI(0, 90);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(img, "PNG", baos);

                    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(baos.toByteArray());
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Arquivo não encontrado"));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao criar capa do arquivo"));
        }
    }

    @GetMapping(value = "/folders/{foldername}/pdfs/{filename:.+}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> getFolderPdfFile(@PathVariable String foldername, @PathVariable String filename){
        try{
            Path targetPath = pdfsDirPath.resolve(foldername + "/" + filename).normalize();

            if(!targetPath.startsWith(pdfsDirPath) || !Files.exists(targetPath)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Resource resource = new FileSystemResource(targetPath);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header("Content-Disposition", "inline; filename=\"" + filename + "\"").body(resource);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}