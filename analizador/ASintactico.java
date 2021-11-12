package analizador;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import analizador.ALexico.Token;

public class ASintactico {

    private ArrayList<Token> listaTokens;
    private Token token;
    private int i;
    private FileWriter fichero;
    private PrintWriter pw;

    public ASintactico(ArrayList<Token> listaTokens) {
        this.listaTokens = listaTokens;
        i = -1;
    }

    private Token siguiente() {
        i++;
        return listaTokens.get(i);
    }

    private void equipara(String t) {
        if (token.getNombre().equals(t)) {
            token = siguiente();
        } else {
            try {
                if (null != fichero)
                    fichero.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            System.out.println("Error sintactico");
            System.exit(0); // Se detiene la ejecucion del proceso (en este caso el ASintactico)
        }
    }

    private String nombre() {
        return token.getNombre();
    }

    private void p() {
        if (nombre().equals("let") || nombre().equals("id") || nombre().equals("if") || nombre().equals("print")
                || nombre().equals("input") || nombre().equals("return")) {
            imprimir(1);
            b();
            p();
        } else if (nombre().equals("function")) {
            imprimir(2);
            f();
            p();
        } else if (!token.getNombre().equals("$")) {
            System.out.println("Error sintactico");
            System.exit(0); // Se detiene la ejecucion del proceso (en este caso el ASintactico)
        }
        imprimir(3); // Solo desde p se llega a fin de fichero "$"
    }

    private void b() {
        if (nombre().equals("let")) {
            imprimir(4);
            equipara(nombre());
            t();
            equipara("id");
            equipara(";");
        } else if (nombre().equals("if")) {
            imprimir(5);
            equipara(nombre());
            g();
        } else if (nombre().equals("id") || nombre().equals("input") || nombre().equals("return")
                || nombre().equals("print")) {
            imprimir(6);
            s();
        }
    }

    private void t() {
        if (nombre().equals("int")) {
            imprimir(7);
            equipara(nombre());
        } else if (nombre().equals("string")) {
            imprimir(8);
            equipara(nombre());
        } else if (nombre().equals("boolean")) {
            imprimir(9);
            equipara(nombre());
        }
    }

    private void g() {
        if (nombre().equals("(")) {
            imprimir(10);
            equipara(nombre());
            e();
            equipara(")");
            s();
        } else if (nombre().equals("{")) {
            imprimir(11);
            equipara(nombre());
            c();
            equipara("}");
            o();
        }
    }

    private void c() {
        if (nombre().equals("let") || nombre().equals("id") || nombre().equals("if") || nombre().equals("print")
                || nombre().equals("input") || nombre().equals("return")) {
            imprimir(12);
            b();
            c();
        } else if (nombre().equals("}")) {
            imprimir(13);
        }
    }

    public void o() {
        if (nombre().equals("else")) {
            imprimir(14);
            equipara(nombre());
            equipara("{");
            c();
            equipara("}");
        } else if (nombre().equals("let") || nombre().equals("id") || nombre().equals("if") || nombre().equals("print")
                || nombre().equals("input") || nombre().equals("return") || nombre().equals("function")) {
            imprimir(15);
        }
    }

    public void s(){
        if(nombre().equals("id")){
            imprimir(16);
            equipara(nombre());
            w();
        }
        else if(nombre().equals("print")){
            imprimir(17);
            equipara(nombre());
            equipara("(");
            e();
            equipara(")");
            equipara(";");
        }
        else if(nombre().equals("input")){
            imprimir(18);
            equipara(nombre());
            equipara("(");
            equipara("id");
            equipara(")");
            equipara(";");
        }
        else if(nombre().equals("return")){
            imprimir(19);
            equipara(nombre());
            x();
            equipara(";");
        }
    }

    public void w(){
        if(nombre().equals("resta") || nombre().equals("asig")){
            
        }
    }

    public void aSintactico() {
        crearParse();
        token = siguiente(); // Primer token
        p();

        try {
            if (null != fichero)
                fichero.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    private void crearParse() {
        fichero = null;
        try {
            fichero = new FileWriter("Parse.txt");
            pw = new PrintWriter(fichero);
            pw.print("Descendente ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void imprimir(int parse) {
        try {
            pw.print(parse + " ");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fichero)
                    fichero.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}
