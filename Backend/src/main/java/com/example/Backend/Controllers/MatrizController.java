package com.example.Backend.Controllers;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MatrizController {

    @PostMapping("/convert")
    public Map<String, String> convertText(@RequestBody Map<String, String> payload) {
        String mode = payload.get("mode");
        String text = payload.get("text");
        String matrix = payload.get("matrix");

        String result = "Resultado de " + mode + " con " + matrix + "Texto: " + text;

        Map<String, String> response = new HashMap<>();
        response.put("result", result);

        return response;
    }
}
