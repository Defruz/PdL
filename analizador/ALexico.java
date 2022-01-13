package analizador;

import java.io.*;
import java.util.*;

public class ALexico {

    // Definicion variables a usar en el programa
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private char caracter;
    private Character aux;
    private int estado;
    private String lexema;
    private int valor;
    private int contador;
    private ArrayList<Token> listaTokens = new ArrayList<>();
    private int posTS;
    private int linea;
    private TablaS tabla;
    private boolean zonaDecl;

    public ALexico(String fichero, TablaS tabla) {
        try {
            fileReader = new FileReader(fichero);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bufferedReader = new BufferedReader(fileReader);
        // tablaGlobal = new ArrayList<>();
        this.tabla = tabla;
        contador = 0;
        estado = 0;
        posTS = 1;
        linea = 1;
        aux = null;
        zonaDecl = false;
    }

    public int getLinea(){
        return this.linea;
    }

    // Definicion del objeto Token que se generara dependiendo del atributo
    public class Token {
        private String nombre;
        private String atributoS;
        private int atributoI;
        private String tabla;

        private Token(String nombre, String atributoS) {
            this.nombre = nombre;
            this.atributoS = atributoS;
        }

        private Token(String nombre, int atributoI, String tabla) {
            this.nombre = nombre;
            this.atributoI = atributoI;
            this.tabla = tabla;
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

        public String getTabla(){
            return this.tabla;
        }
    }

    public Token generarToken(String nombre, String atributo) {
        Token token = new Token(nombre, atributo);
        listaTokens.add(token);
        return token;
    }

    private Token generarToken(String nombre, int atributo, String tabla) {
        Token token = new Token(nombre, atributo, tabla);
        listaTokens.add(token);
        return token;
    }

    public ArrayList<Token> getLista() {
        return listaTokens;
    }

    // Inicializar la lista de palabras reservadas
    private String[] palabras = new String[] { "if", "else", "print", "input", "return", "string", "int", "boolean",
            "let" };
    private ArrayList<String> pReservadas = new ArrayList<>(Arrays.asList(palabras));
    // private ArrayList<String> tablaGlobal;

    public Token aLexico() {
        try {
            while (bufferedReader.ready() || aux != null) {
                if (aux != null) {
                    caracter = aux;
                    aux = null;
                } else {
                    caracter = (char) bufferedReader.read();
                }
                switch (estado) {
                    case 0:
                        switch (caracter) {
                            case ';':
                                return generarToken("puntComa", "");
                            case ',':
                                return generarToken("coma", "");
                            case '}':
                                return generarToken("kCerrada", "");
                            case '{':
                                return generarToken("kAbierta", "");
                            case ')':
                                return generarToken("pCerrado", "");
                            case '(':
                                return generarToken("pAbierto", "");
                            case '&':
                                estado = 7;
                                break;
                            case '+':
                                return generarToken("suma", "");
                            case '>':
                                return generarToken("mayor", "");
                            case '<':
                                return generarToken("menor", "");
                            case '=':
                                return generarToken("asig", "");
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
                                            "Error Léxico Linea " + linea
                                                    + ": ningun token puede empezar por el caracter '_'");
                                } else if (caracter == 13) {
                                    linea++;
                                } else if (caracter != 32 & caracter != 10 && caracter != 9) {
                                    bufferedReader.readLine();
                                    System.out.println("Error Léxico Linea " + linea + ": entrada no reconocida");
                                    linea++;
                                }
                        }
                        break;

                    case 1:
                        if (Character.isLetterOrDigit(caracter) || caracter == '_') {
                            lexema += caracter;
                        } else if (lexema.equals("function")) {
                            estado = 0;
                            aux = caracter;
                            return generarToken(lexema, "");
                        } else if (pReservadas.contains(lexema)) {
                            estado = 0;
                            aux = caracter;
                            return generarToken(lexema, "");
                        } else {
                            estado = 0;
                            posTS = tabla.indexOfLex(lexema);
                            if (posTS < 0) {
                                if (zonaDecl) {
                                    // tablaGlobal.add(lexema);
                                    tabla.insertaLexema(lexema);
                                    aux = caracter;
                                    return generarToken("id", tabla.getTSActualSize(), "actual");
                                } else {
                                    posTS = tabla.indexOfLexG(lexema);
                                    if (posTS < 0) {
                                        // tablaGlobal.add(lexema);
                                        tabla.insertaGlobal(lexema);
                                        aux = caracter;
                                        return generarToken("id", tabla.getTSGlobalSize(), "global");
                                    } else {
                                        aux = caracter;
                                        return generarToken("id", posTS + 1, "global");
                                    }
                                }
                            } else {
                                if (zonaDecl) {
                                    System.out.println("Error Semantico en la linea " + getLinea() + "al redeclarar una variable");
                                }
                                aux = caracter;
                                return generarToken("id", posTS + 1, "actual");
                            }
                        }
                        break;

                    case 2:
                        if (Character.isDigit(caracter)) {
                            valor = (valor * 10) + Character.getNumericValue(caracter);
                        } else {
                            estado = 0;
                            if (valor < 32768) {
                                aux = caracter;
                                return generarToken("numEnt", valor, "");
                            } else {
                                System.out.println(
                                        "Error Lexico Linea " + linea + ": el valor supera el entero maximo del lenguaje");
                            }
                        }
                        break;

                    case 3:
                        estado = 0;
                        if (caracter == '=') {
                            return generarToken("asigResta", "");
                        } else {
                            aux = caracter;
                            return generarToken("resta", "");
                        }

                    case 4:
                        if (caracter == '/') {
                            estado = 5;
                        } else {
                            estado = 0;
                            bufferedReader.readLine();
                            System.out.println("Error Léxico Linea " + linea + ": entrada no reconocida");
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
                                        "Error Léxico Linea " + linea + ": la cadena ha excedido el maximo de caracteres.");
                                linea++;
                            }
                        } else {
                            estado = 0;
                            return generarToken("cadena", lexema);
                        }
                        break;

                    case 7:
                        if (caracter == '&') {
                            estado = 0;
                            return generarToken("and", "");
                        } else {
                            estado = 0;
                            bufferedReader.readLine();
                            System.out.println("Error Léxico Linea " + linea
                                    + ": se ha recibido solamente un solo caracter '&', entrada no valida");
                            linea++;
                        }
                }
            }
            return generarToken("$", "");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void actualizarZonaDecl(boolean act) {
        zonaDecl = act;
    }

    public void imprimirTokens() {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("Tokens.txt");
            pw = new PrintWriter(fichero);
            for (Token imp : listaTokens) {
                if (!imp.getNombre().equals("$")) {
                    if (imp.atributoS != null) {
                        pw.println("<" + imp.getNombre() + "," + imp.getAtributoS() + ">");
                    } else {
                        pw.println("<" + imp.getNombre() + "," + imp.getAtributoI() + ">");
                    }
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
