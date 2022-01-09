package analizador;

import java.io.File;

public class Principal {
    public static void main(String[] args) {
        String filePath = new File("").getAbsolutePath();
        filePath = filePath.concat("\\pruebas\\prueba17.txt");
        ALexico aLex = new ALexico(filePath);
        ASinSem aSin = new ASinSem(aLex);
        aSin.aSintactico();
    }
}
