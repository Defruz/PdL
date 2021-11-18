package analizador;

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
            System.out.println("Error sintactico" + " " + token.getNombre() + " " + t);
            System.exit(0); // Se detiene la ejecucion del proceso (en este caso el ASintactico)
        }
        System.out.println(token.getNombre() + " " + t);
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
        else{
            imprimir(3); // Solo desde p se llega a fin de fichero "$"
        }
        
    }

    private void b() {
        if (nombre().equals("let")) {
            imprimir(4);
            equipara(nombre());
            t();
            equipara("id");
            equipara("puntComa");
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
        if (nombre().equals("pAbierto")) {
            imprimir(10);
            equipara(nombre());
            e();
            equipara("pCerrado");
            s();
        } else if (nombre().equals("kAbierta")) {
            imprimir(11);
            equipara(nombre());
            c();
            equipara("kCerrada");
            o();
        }
    }

    private void c() {
        if (nombre().equals("let") || nombre().equals("id") || nombre().equals("if") || nombre().equals("print")
                || nombre().equals("input") || nombre().equals("return")) {
            imprimir(12);
            b();
            c();
        } else if (nombre().equals("kCerrada")) {
            imprimir(13);
        }
    }

    private void o() {
        if (nombre().equals("else")) {
            imprimir(14);
            equipara(nombre());
            equipara("kAbierta");
            c();
            equipara("kCerrada");
        } else if (nombre().equals("let") || nombre().equals("id") || nombre().equals("if") || nombre().equals("print")
                || nombre().equals("input") || nombre().equals("return") || nombre().equals("function")) {
            imprimir(15);
        }
    }

    private void s() {
        if (nombre().equals("id")) {
            imprimir(16);
            equipara(nombre());
            w();
        } else if (nombre().equals("print")) {
            imprimir(17);
            equipara(nombre());
            equipara("pAbierto");
            e();
            equipara("pCerrado");
            equipara("puntComa");
        } else if (nombre().equals("input")) {
            imprimir(18);
            equipara(nombre());
            equipara("pAbierto");
            equipara("id");
            equipara("pCerrado");
            equipara("puntComa");
        } else if (nombre().equals("return")) {
            imprimir(19);
            equipara(nombre());
            x();
            equipara("puntComa");
        }
    }

    private void w() {
        if (nombre().equals("asigResta")) {
            imprimir(20);
            equipara(nombre());
            e();
            equipara("puntComa");
        } else if (nombre().equals("asig")) {
            imprimir(21);
            equipara(nombre());
            e();
            equipara("puntComa");
        } else if (nombre().equals("pAbierto")) {
            imprimir(22);
            equipara(nombre());
            l();
            equipara("pCerrado");
            equipara("puntComa");
        }
    }

    private void x() {
        if (nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt")
                || nombre().equals("cadena")) {
            imprimir(23);
            e();
        } else if (nombre().equals("puntComa")) {
            imprimir(24);
            equipara(nombre());
        }
    }

    private void l() {
        if (nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt")
                || nombre().equals("cadena")) {
            imprimir(25);
            e();
            q();
        } else if (nombre().equals("pCerrado")) {
            imprimir(26);
        }
    }

    private void q() {
        if (nombre().equals("coma")) {
            imprimir(27);
            equipara(nombre());
            e();
            q();
        } else if (nombre().equals("pCerrado")) {
            imprimir(28);
        }
    }

    private void f() {
        if (nombre().equals("function")) {
            imprimir(29);
            equipara(nombre());
            equipara("id");
            h();
            equipara("pAbierto");
            a();
            equipara("pCerrado");
            equipara("kAbierta");
            c();
            equipara("kCerrada");
        }
    }

    private void h() {
        if (nombre().equals("int") || nombre().equals("string") || nombre().equals("boolean")) {
            imprimir(30);
            t();
        } else if (nombre().equals("pAbierto")) {
            imprimir(31);
        }
    }

    private void a() {
        if (nombre().equals("int") || nombre().equals("string") || nombre().equals("boolean")) {
            imprimir(32);
            t();
            equipara("id");
            k();
        } else if (nombre().equals("pCerrado")) {
            imprimir(33);
        }
    }

    private void k() {
        if (nombre().equals("coma")) {
            imprimir(34);
            equipara(nombre());
            t();
            equipara("id");
            k();
        } else if (nombre().equals("pCerrado")) {
            imprimir(35);
        }
    }

    private void e() {
        if (nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt")
                || nombre().equals("cadena")) {
            imprimir(36);
            r();
            e_();
        }
    }

    private void e_() {
        if (nombre().equals("and")) {
            imprimir(37);
            equipara(nombre());
            r();
            e_();
        } else if (nombre().equals("puntComa") || nombre().equals("pCerrado") || nombre().equals("coma")) {
            imprimir(38);
        }
    }

    private void r(){
        if(nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt") || nombre().equals("cadena")){
            imprimir(39);
            u();
            r_();
        }
    }

    private void r_() {
        if (nombre().equals("menor")) {
            imprimir(40);
            equipara(nombre());
            u();
            r_();
        } else if (nombre().equals("mayor")) {
            imprimir(41);
            equipara(nombre());
            u();
            r_();
        } else if (nombre().equals("puntComa") || nombre().equals("pCerrado") || nombre().equals("coma")
                || nombre().equals("and")) {
            imprimir(42);
        }
    }

    private void u(){
        if(nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt") || nombre().equals("cadena")){
            imprimir(43);
            v();
            u_();
        }
    }

    private void u_(){
        if(nombre().equals("suma")){
            imprimir(44);
            equipara(nombre());
            v();
            u_();
        }
        else if(nombre().equals("resta")){
            imprimir(45);
            equipara(nombre());
            v();
            u_();
        }
        else if(nombre().equals("puntComa") || nombre().equals("pCerrado") || nombre().equals("coma") || nombre().equals("and") || nombre().equals("mayor") || nombre().equals("menor")){
            imprimir(46);
        }
    }

    private void v(){
        if(nombre().equals("id")){
            imprimir(47);
            equipara(nombre());
            d();
        }
        else if(nombre().equals("pAbierto")){
            imprimir(48);
            equipara(nombre());
            e();
            equipara("pCerrado");
        }
        else if(nombre().equals("numEnt")){
            imprimir(49);
            equipara(nombre());
        }
        else if(nombre().equals("cadena")){
            imprimir(50);
            equipara(nombre());
        }
    }

    private void d(){
        if(nombre().equals("pAbierto")){
            imprimir(51);
            equipara(nombre());
            l();
            equipara("pCerrado");
        }
        else if(nombre().equals("puntComa") || nombre().equals("pCerrado") || nombre().equals("coma") || nombre().equals("and") || nombre().equals("mayor") || nombre().equals("menor") || nombre().equals("suma") || nombre().equals("resta")){
            imprimir(52);
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
        }
    }

}
