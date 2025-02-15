import java.util.Random;
import java.util.Scanner;

public class Main {
  private static int[][] keyMatrix;
  private static int[][] inverseMatrix;
  private static int matrixSize;

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Ingrese el mensaje:");
    String message = scanner.nextLine().toUpperCase().replaceAll("[^A-Z]", "");

    System.out.println("Seleccione el tamaño de la matriz (1: 2x2, 2: 3x3, 3: 4x4):");
    int option = scanner.nextInt();

    System.out.println("Seleccione la acción (1: Cifrar, 2: Descifrar):");
    int action = scanner.nextInt();

    initializeMatrix(option);

    if (action == 1) {
      String encrypted = encrypt(message);
      System.out.println("Mensaje cifrado: " + encrypted);
    } else {
      System.out.println("Ingrese el mensaje cifrado:");
      scanner.nextLine(); // Consumir la nueva línea
      String encryptedMessage = scanner.nextLine().toUpperCase().replaceAll("[^A-Z]", "");
      String decrypted = decrypt(encryptedMessage);
      System.out.println("Mensaje descifrado: " + decrypted);
    }
  }

  private static void initializeMatrix(int option) {
    matrixSize = option + 1;
    keyMatrix = new int[matrixSize][matrixSize];
    Random random = new Random();

    do {
      for (int i = 0; i < matrixSize; i++) {
        for (int j = 0; j < matrixSize; j++) {
          keyMatrix[i][j] = random.nextInt(25) + 1; // Evitar 0
        }
      }
    } while (determinant(keyMatrix, matrixSize) == 0);

    for (int i = 0; i < matrixSize; i++) {
      for (int j = 0; j < matrixSize; j++) {
        System.out.print(keyMatrix[i][j]+" ");
      }
      System.out.println();
    }

    System.out.println("Determinante = "+determinant(keyMatrix, matrixSize));
    inverseMatrix = invertMatrix(keyMatrix);
  }

  private static String encrypt(String text) {
    return processText(text, keyMatrix);
  }

  private static String decrypt(String text) {
    return processText(text, inverseMatrix);
  }

  private static String processText(String text, int[][] matrix) {
    StringBuilder result = new StringBuilder();
    text = padText(text);

    for (int i = 0; i < text.length(); i += matrixSize) {
      int[] vector = new int[matrixSize];
      for (int j = 0; j < matrixSize; j++) {
        vector[j] = text.charAt(i + j) - 'A';
      }

      int[] transformedVector = multiplyMatrix(matrix, vector);
      for (int value : transformedVector) {
        result.append((char) ('A' + (value % 26)));
      }
    }

    return result.toString();
  }

  private static String padText(String text) {
    while (text.length() % matrixSize != 0) {
      text += 'X';
    }
    return text;
  }

  private static int[] multiplyMatrix(int[][] matrix, int[] vector) {
    int[] result = new int[matrixSize];
    for (int i = 0; i < matrixSize; i++) {
      for (int j = 0; j < matrixSize; j++) {
        result[i] += matrix[i][j] * vector[j];
      }
    }
    return result;
  }

  private static int determinant(int[][] matrix, int size) {
    if (size == 2) {
      return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
    }
    int det = 0;
    for (int i = 0; i < size; i++) {
      int[][] subMatrix = new int[size - 1][size - 1];
      for (int j = 1; j < size; j++) {
        for (int k = 0, col = 0; k < size; k++) {
          if (k == i) continue;
          subMatrix[j - 1][col++] = matrix[j][k];
        }
      }
      det += Math.pow(-1, i) * matrix[0][i] * determinant(subMatrix, size - 1);
    }
    return det;
  }

  private static int[][] invertMatrix(int[][] matrix) {
    // Implementación básica de matriz inversa en módulo 26 (no incluida por simplicidad)
    return new int[matrixSize][matrixSize]; // Debe calcular la inversa modular
  }
}
