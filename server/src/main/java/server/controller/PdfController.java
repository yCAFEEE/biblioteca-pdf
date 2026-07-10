package server.controller;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class PdfController{
    private final String pdfsDir = "../client/public/pdfDir";

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

                    Map<String, String> pdfInfo = new HashMap<>();
                    pdfInfo.put("name", name);
                    pdfInfo.put("url", "/pdfs-files/" + encodedName);

                    list.add(pdfInfo);
                }
            }
            return ResponseEntity.ok(list);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao listar os arquivos PDFs"));
        }
    }
    
}