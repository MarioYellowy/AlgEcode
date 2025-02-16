package com.example.Backend.Controllers;

import com.example.Backend.Services.MatrisServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MatrizController {

    private int[][] lastKeyMatrix;
    private int lastDeterminante;
    private int lastKeySize;

    @PostMapping("/convert")
    public ResponseEntity<Map<String, Object>> convertText(@RequestBody Map<String, Object> payload) {
        String mode = (String) payload.get("mode");
        String text = (String) payload.get("text");

        int matrixOption = 3;
        if (payload.get("matrix") instanceof Number) {
            matrixOption = ((Number) payload.get("matrix")).intValue();
        }

        int keySize;
        switch (matrixOption) {
            case 1:
                keySize = 2;
                break;
            case 2:
                keySize = 3;
                break;
            case 3:
                keySize = 4;
                break;
            default:
                keySize = 3;
        }

        Map<String, Object> response = new HashMap<>();

        if ("encode".equalsIgnoreCase(mode)) {
            String prepared = MatrisServices.prepareText(text);
            prepared = MatrisServices.padText(prepared, keySize);

            int[][] keyMatrix = MatrisServices.generateKeyMatrix(keySize);
            StringBuilder encrypted = new StringBuilder();
            for (int i = 0; i < prepared.length(); i += keySize) {
                String block = prepared.substring(i, i + keySize);
                encrypted.append(MatrisServices.encryptBlock(block, keyMatrix));
            }

            List<List<Integer>> keyMatrixList = new ArrayList<>();
            for (int i = 0; i < keyMatrix.length; i++) {
                List<Integer> row = new ArrayList<>();
                for (int j = 0; j < keyMatrix[i].length; j++) {
                    row.add(keyMatrix[i][j]);
                }
                keyMatrixList.add(row);
            }

            lastKeyMatrix = keyMatrix;
            lastKeySize = keySize;
            lastDeterminante = MatrisServices.calculateDeterminant(keyMatrix, keySize);

            response.put("result", encrypted.toString());
            response.put("keyMatrix", keyMatrixList);
            return ResponseEntity.ok(response);

        } else if ("decode".equalsIgnoreCase(mode)) {
            Object keyMatrixObj = payload.get("keyMatrix");
            if (keyMatrixObj == null) {
                response.put("error", "Para descifrar, es necesario proporcionar la clave (keyMatrix).");
                return ResponseEntity.badRequest().body(response);
            }

            List<?> matrixList = (List<?>) keyMatrixObj;
            keySize = matrixList.size();
            int[][] keyMatrix = new int[keySize][keySize];
            try {
                for (int i = 0; i < keySize; i++) {
                    List<?> rowList = (List<?>) matrixList.get(i);
                    for (int j = 0; j < keySize; j++) {
                        Number num = (Number) rowList.get(j);
                        keyMatrix[i][j] = num.intValue();
                    }
                }
            } catch (Exception e) {
                response.put("error", "Formato de keyMatrix inválido.");
                return ResponseEntity.badRequest().body(response);
            }

            int[][] inverseMatrix = MatrisServices.calculateInverseMatrix(keyMatrix);
            StringBuilder decrypted = new StringBuilder();
            for (int i = 0; i < text.length(); i += keySize) {
                String block = text.substring(i, Math.min(i + keySize, text.length()));
                if (block.length() < keySize) {
                    block = MatrisServices.padText(block, keySize);
                }
                decrypted.append(MatrisServices.decryptBlock(block, inverseMatrix));
            }

            lastKeyMatrix = keyMatrix;
            lastKeySize = keySize;
            lastDeterminante = MatrisServices.calculateDeterminant(keyMatrix, keySize);

            response.put("result", decrypted.toString());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Modo no reconocido. Use 'encode' o 'decode'.");
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/detalles")
    public ResponseEntity<Map<String, Object>> getDetalles() {
        if (lastKeyMatrix == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "No se ha realizado ninguna operación todavía.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        List<List<Integer>> matrizList = new ArrayList<>();
        for (int i = 0; i < lastKeyMatrix.length; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < lastKeyMatrix[i].length; j++) {
                row.add(lastKeyMatrix[i][j]);
            }
            matrizList.add(row);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("determinante", lastDeterminante);
        response.put("matriz", matrizList);
        response.put("keySize", lastKeySize);
        return ResponseEntity.ok(response);
    }
}
