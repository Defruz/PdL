package analizador;

import java.io.File;

public class Principal {
    public static void main(String[] args) {
        String filePath = new File("").getAbsolutePath();
        filePath = filePath.concat("\\corrector\\prueba30.txt");
        TablaS tabla = new TablaS();
        ALexico aLex = new ALexico(filePath, tabla);
        ASinSem aSin = new ASinSem(aLex, tabla);
        aSin.aSintactico();
        aLex.imprimirTokens();
    }
}
