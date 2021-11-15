package analizador;

import java.io.*;
import java.util.*;

public class ALexico {

    // Definicion variables a usar en el programa
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private char caracter;
    private int estado;
    private String lexema;
    private int valor;
    private int contador;
    private ArrayList<Token> listaTokens = new ArrayList<>();
    private int posTS;
    private int linea;

    // Definicion del objeto Token que se generara dependiendo del atributo
    public class Token {
        private String nombre;
        private String atributoS;
        private int atributoI;

        private Token(String nombre, String atributoS) {
            this.nombre = nombre;
            this.atributoS = atributoS;
        }

        private Token(String nombre, int atributoI) {
            this.nombre = nombre;
            this.atributoI = atributoI;
        }

        public String getNombre() {
            return this.nombre;
        }

        public int getAtributoI() {
            return this.atributoI;
        }

        public String getAtributoS() {
            return this.atributoS;
        }
    }

    public void generarToken(String nombre, String atributo) {
        Token token = new Token(nombre, atributo);
        listaTokens.add(token);
    }

    private void generarToken(String nombre, int atributo) {
        Token token = new Token(nombre, atributo);
        listaTokens.add(token);
    }

    public ArrayList<Token> getLista(){
        return listaTokens;
    }

    // Inicializar la lista de palabras reservadas
    private String[] palabras = new String[] { "if", "else", "print", "input", "return", "string", "int", "boolean",
            "let" };
    private ArrayList<String> pReservadas = new ArrayList<>(Arrays.asList(palabras));
    private ArrayList<String> tablaGlobal;

    public void aLexico(String fichero) {
        try {
            fileReader = new FileReader(fichero);
            bufferedReader = new BufferedReader(fileReader);
            contador = 0;
            estado = 0;
            posTS = 1;
            tablaGlobal = new ArrayList<>();
            linea = 1;

            while (bufferedReader.ready()) {
                caracter = (char) bufferedReader.read();
                switch (estado) {
                case 3:
                    estado = 0;
                    if (caracter == '=') {
                        generarToken("asigResta", "");
                        break;
                    } else {
                        generarToken("resta", "");
                    }
                case 0:
                    switch (caracter) {
                    case ';':
                        generarToken("puntComa", "");
                        break;

                    case ',':
                        generarToken("coma", "");
                        break;

                    case '}':
                        generarToken("kCerrada", "");
                        break;

                    case '{':
                        generarToken("kAbierta", "");
                        break;

                    case ')':
                        generarToken("pCerrado", "");
                        break;

                    case '(':
                        generarToken("pAbierto", "");
                        break;

                    case '&':
                        estado = 7;
                        break;

                    case '+':
                        generarToken("suma", "");
                        break;

                    case '>':
                        generarToken("mayor", "");
                        break;

                    case '<':
                        generarToken("menor", "");
                        break;

                    case '=':
                        generarToken("asig", "");
                        break;

                    case '\'':
                        contador = 0;
                        lexema = "";
                        estado = 6;
                        break;

                    case '/':
                        estado = 4;
                        break;

                    case '-':
                        estado = 3;
                        lexema = "-";
                        break;

                    default:

                        if (Character.isDigit(caracter)) {
                            estado = 2;
                            valor = Character.getNumericValue(caracter);
                        } else if (Character.isLetter(caracter)) {
                            estado = 1;
                            lexema = "" + caracter;
                        } else if (caracter == '_') {
                            System.out.println(
                                    "Error Linea " + linea + ": ningun token puede empezar por el caracter '_'");
                        } else if (caracter == 13) {
                            linea++;
                        } else if (caracter != 32 & caracter != 10 && caracter != 9) {
                            bufferedReader.readLine();
                            System.out.println("Error Linea " + linea + ": entrada no reconocida");
                            linea++;
                        }
                    }

                    break;

                case 1:
                    if (Character.isLetterOrDigit(caracter) || caracter == '_') {
                        lexema += caracter;
                    } else if (lexema.equals("function")) {
                        estado = 0;
                        generarToken(lexema, "");
                        estadoAux(caracter);
                    } else if (pReservadas.contains(lexema)) {
                        estado = 0;
                        generarToken(lexema, "");
                        estadoAux(caracter);
                    } else {
                        estado = 0;
                        if (!tablaGlobal.contains(lexema)) {
                            tablaGlobal.add(lexema);
                            generarToken("id", posTS);
                            posTS++;
                        } else {
                            int pos = tablaGlobal.indexOf(lexema) + 1;
                            generarToken("id", pos);
                        }
                        estadoAux(caracter);
                    }

                    break;

                case 2:
                    if (Character.isDigit(caracter)) {
                        valor = (valor * 10) + Character.getNumericValue(caracter);
                    } else {
                        estado = 0;
                        if (valor < 32768) {
                            generarToken("numEnt", valor);
                            estadoAux(caracter);
                        } else {
                            System.out.println(
                                    "Error Linea " + linea + ": el valor supera el entero maximo del lenguaje");
                        }
                    } // if (simbolosLeng.contains(caracter) || Character.isLetter(caracter)) {

                    /*
                     * else { estado = 0; bufferedReader.readLine();
                     * System.out.println("Error Linea " + linea +
                     * ": entrada no reconocida.Fallo en la linea"); linea++; }
                     */
                    break;

                case 4:
                    if (caracter == '/') {
                        estado = 5;
                    } else {
                        estado = 0;
                        bufferedReader.readLine();
                        System.out.println("Error Linea " + linea + ": entrada no reconocida");
                        linea++;
                    }
                    break;

                case 5:
                    estado = 0;
                    bufferedReader.readLine();
                    linea++;
                    break;

                case 6:
                    if (caracter != '\'') {
                        lexema += caracter;
                        contador++;
                        if (contador > 64) {
                            estado = 0;
                            bufferedReader.readLine();
                            System.out.println(
                                    "Error Linea " + linea + ": la cadena ha excedido el maximo de caracteres.");
                            linea++;
                        }
                    } else {
                        estado = 0;

                        generarToken("cadena", lexema);
                    }
                    break;

                case 7:
                    if(caracter == '&'){   
                        generarToken("and", "");
                        estado = 0;
                    }
                    else {
                        estado = 0;
                        bufferedReader.readLine();
                            System.out.println(
                                    "Error Linea " + linea + ": se ha recibido solamente un solo caracter '&', entrada no valida");
                            linea++;
                    }
                }

            }
            imprimirTabla();
            imprimirTokens();

        } catch (FileNotFoundException e) {
            System.out.println("Error: el archivo no existe o no puede ser leido");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void imprimirTokens() {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("Tokens.txt");
            pw = new PrintWriter(fichero);
            for (Token imp : listaTokens) {
                if (imp.atributoS != null) {
                    pw.println("<" + imp.getNombre() + "," + imp.getAtributoS() + ">");
                } else {
                    pw.println("<" + imp.getNombre() + "," + imp.getAtributoI() + ">");
                }
            }
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

    private void estadoAux(char Caracter) {
        switch (caracter) {
        case ';':
            generarToken("puntComa", "");
            break;

        case ',':
            generarToken("coma", "");
            break;

        case '}':
            generarToken("kCerrada", "");
            break;

        case '{':
            generarToken("kabierta", "");
            break;

        case ')':
            generarToken("pCerrado", "");
            break;

        case '(':
            generarToken("pAbierto", "");
            break;

        case '!':
            generarToken("negacion", "");
            break;

        case '+':
            generarToken("suma", "");
            break;

        case '>':
            generarToken("mayor", "");
            break;

        case '<':
            generarToken("menor", "");
            break;

        case '=':
            generarToken("asig", "");
            break;

        case '\'':
            contador = 0;
            lexema = "";
            estado = 6;
            break;

        case '/':
            estado = 4;
            break;

        case '-':
            estado = 3;
            lexema = "-";
            break;

        default:

            if (Character.isDigit(caracter)) {
                estado = 2;
                valor = Character.getNumericValue(caracter);
            } else if (Character.isLetter(caracter)) {
                estado = 1;
                lexema = "" + caracter;
            } else if (caracter == 13) {
                linea++;
            } else if (caracter != 32 & caracter != 10 && caracter != 9) {
                System.out.println("Error Linea " + linea + ": entrada no reconocida");
            }
        }
    }

    private void imprimirTabla() {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("TS.txt");
            pw = new PrintWriter(fichero);
            pw.println("TablaGlobal #01:");
            for (String imp : tablaGlobal) {
                {
                    pw.println("*'" + imp + "'");
                }
            }
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
