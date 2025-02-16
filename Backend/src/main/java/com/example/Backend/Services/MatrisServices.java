package com.example.Backend.Services;

import java.util.Random;

import lombok.Getter;
import org.springframework.stereotype.Service;


@Service
public class MatrisServices {
    @Getter
    private static int[][] lastKeyMatrix;

    public static String prepareText(String text) {
        return text.toUpperCase().replaceAll("[^A-Z]", "");
    }

    public static String padText(String text, int matrixSize) {
        while (text.length() % matrixSize != 0) {
            text += 'X';  // Agregar 'X' para rellenar
        }
        return text;
    }

    public static int[][] generateKeyMatrix(int size) {
        Random random = new Random();
        int[][] matrix = new int[size][size];
        
        do {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = random.nextInt(25) + 1;
                }
            }
        } while (calculateDeterminant(matrix, size) == 0);  // Asegurar que el determinante no sea 0

        lastKeyMatrix = matrix;
        return matrix;
    }

    public static int calculateDeterminant(int[][] matrix, int size) {
        if (size == 1) return matrix[0][0];
        if (size == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }
        
        int det = 0;
        for (int i = 0; i < size; i++) {
            det += Math.pow(-1, i) * matrix[0][i] * 
                  calculateDeterminant(getSubMatrix(matrix, 0, i), size - 1);
        }
        return det;
    }

    public static int[][] getSubMatrix(int[][] matrix, int row, int col) {
        int size = matrix.length;
        int[][] subMatrix = new int[size-1][size-1];
        int r = 0, c = 0;
        
        for (int i = 0; i < size; i++) {
            if (i == row) continue;
            c = 0;
            for (int j = 0; j < size; j++) {
                if (j == col) continue;
                subMatrix[r][c] = matrix[i][j];
                c++;
            }
            r++;
        }
        return subMatrix;
    }

    public static int[] multiplyMatrixVector(int[][] matrix, int[] vector) {
        int size = matrix.length;
        int[] result = new int[size];
        
        for (int i = 0; i < size; i++) {
            result[i] = 0;
            for (int j = 0; j < size; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
            result[i] = result[i] % 26;
        }
        return result;
    }

    public static int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return 1;
    }

    public static int[][] calculateAdjMatrix(int[][] matrix) {
        int size = matrix.length;
        int[][] adj = new int[size][size];
        
        if (size == 1) {
            adj[0][0] = 1;
            return adj;
        }
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int[][] temp = getSubMatrix(matrix, i, j);
                adj[j][i] = (int) (Math.pow(-1, i + j) * calculateDeterminant(temp, size - 1));
            }
        }
        return adj;
    }

    public static int[][] calculateInverseMatrix(int[][] matrix) {
        int size = matrix.length;
        int det = calculateDeterminant(matrix, size);
        det = ((det % 26) + 26) % 26;
        
        int detInv = modInverse(det, 26);
        int[][] adj = calculateAdjMatrix(matrix);
        int[][] inv = new int[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                inv[i][j] = ((adj[i][j] * detInv) % 26 + 26) % 26;
            }
        }
        return inv;
    }

    public static String encryptBlock(String block, int[][] keyMatrix) {
        int[] vector = new int[block.length()];
        for (int i = 0; i < block.length(); i++) {
            vector[i] = block.charAt(i) - 'A';
        }
        
        int[] result = multiplyMatrixVector(keyMatrix, vector);
        StringBuilder encrypted = new StringBuilder();
        
        for (int value : result) {
            encrypted.append((char) (value + 'A'));
        }
        return encrypted.toString();
    }

    public static String decryptBlock(String block, int[][] inverseMatrix) {
        return encryptBlock(block, inverseMatrix);
    }

    public static String encrypt(String plaintext, int keySize) {
        String prepared = prepareText(plaintext);
        prepared = padText(prepared, keySize);

        int[][] keyMatrix = generateKeyMatrix(keySize);
        StringBuilder encrypted = new StringBuilder();

        for (int i = 0; i < prepared.length(); i += keySize) {
            String block = prepared.substring(i, i + keySize);
            encrypted.append(encryptBlock(block, keyMatrix));
        }
        return encrypted.toString();
    }

    public static String decrypt(String ciphertext, int[][] keyMatrix) {
        int[][] inverseMatrix = calculateInverseMatrix(keyMatrix);
        StringBuilder decrypted = new StringBuilder();
        int keySize = keyMatrix.length;

        for (int i = 0; i < ciphertext.length(); i += keySize) {
            String block = ciphertext.substring(i, i + keySize);
            decrypted.append(decryptBlock(block, inverseMatrix));
        }
        return decrypted.toString();
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
}
