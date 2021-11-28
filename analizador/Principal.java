package analizador;

import java.io.File;

public class Principal {
    public static void main(String[] args) {
        String filePath = new File("").getAbsolutePath();
        filePath = filePath.concat("\\pruebas\\prueba12.txt");
        ALexico aLex = new ALexico();
        aLex.aLexico(filePath);
        aLex.generarToken("$", ""); //Se añade fin de fichero al terminar la lista de tokens
        ASintactico aSin = new ASintactico(aLex.getLista());
        aSin.aSintactico();
    }
}
