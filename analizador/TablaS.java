package analizador;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class TablaS {
    private ArrayList<HashMap<String, String>> tablaGlobal;
    private ArrayList<HashMap<String, String>> tablaActual;
    private int contador;
    private ArrayList<ArrayList<HashMap<String, String>>> impreso;

    public TablaS() {
        tablaGlobal = new ArrayList<HashMap<String, String>>();
        tablaActual = tablaGlobal;
        contador = 1;
        impreso = new ArrayList<ArrayList<HashMap<String, String>>>();
    }

    public void setTSActual() {
        this.tablaActual = new ArrayList<>();
    }

    public int getTSActualSize() {
        return tablaActual.size();
    }

    public int getTSGlobalSize(){
        return tablaGlobal.size();
    }

    public void destruyeTS() {
        guardarTabla();
        this.tablaActual = tablaGlobal;
    }

    public void insertaLexema(String nombre) {
        tablaActual.add(new HashMap<>());
        tablaActual.get(tablaActual.size() - 1).put("lexema", nombre);
    }

    public void insertaGlobal(String nombre) {
        tablaGlobal.add(new HashMap<>());
        tablaGlobal.get(tablaGlobal.size() - 1).put("lexema", nombre);
    }

    public void insertaTipoTS(String nombre, int pos, String tipo) {
        if(nombre.equals("global")){
            tablaGlobal.get(pos - 1).put("tipo", tipo);
        }else{
            tablaActual.get(pos - 1).put("tipo", tipo);
        }
        
    }

    public void insertaDespTS(String nombre, int pos, String desp) {
        if(nombre.equals("global")){
            tablaGlobal.get(pos - 1).put("despl", desp);
        }
        else{
            tablaActual.get(pos - 1).put("despl", desp);
        }
    }

    public void insertaTipoRet(int pos, String ret) {
        tablaGlobal.get(pos - 1).put("tipoRetorno", ret);
    }

    public void insertaTipoParam(int pos, String param) {
        tablaGlobal.get(pos - 1).put("tipoParam", param);
    }

    public void insertaEtTS(int pos) {
        tablaGlobal.get(pos - 1).put("tipo", "function");
        tablaGlobal.get(pos - 1).put("etiqFuncion", nuevaEtiq(tablaGlobal.get(pos - 1).get("lexema")));
    }

    private String nuevaEtiq(String nombre) {
        return "Et" + contador++ + "_" + nombre;
    }

    public int indexOfLex(String nombre) {
        for (int i = 0; i < tablaActual.size(); i++) {
            if (tablaActual.get(i).get("lexema").equals(nombre)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfLexG(String nombre) {
        for (int i = 0; i < tablaGlobal.size(); i++) {
            if (tablaGlobal.get(i).get("lexema").equals(nombre)) {
                return i;
            }
        }
        return -1;
    }

    public String buscaTipo(String nombre, int pos) {
        if (nombre.equals("global")){
            return tablaGlobal.get(pos - 1).get("tipo");
        }
        return tablaActual.get(pos - 1).get("tipo");
    }

    public String buscaTipoRet(int pos) {
        return tablaGlobal.get(pos - 1).get("tipoRetorno");
    }

    public int anchoTipo(String tipo) {
        if (tipo.equals("cadena")) {
            return 64;
        }
        return 1;
    }

    public String buscaParam(int pos) {
        return tablaGlobal.get(pos - 1).get("tipoParam");
    }

    public boolean inTSL (){
        return tablaActual != tablaGlobal;
    }
    private void guardarTabla() {
        impreso.add((ArrayList<HashMap<String, String>>) tablaActual.clone());
    }

    public void imprimirTabla() {
        ArrayList<HashMap<String, String>> tabla;
        FileWriter fichero = null;
        PrintWriter pw = null;
        HashMap<String, String> fila;
        try {
            fichero = new FileWriter("TS.txt");
            pw = new PrintWriter(fichero);
            tabla = impreso.get(impreso.size() - 1);
            pw.print("#1:\n");
            for (int i = 0; i < tabla.size(); i++) {
                fila = tabla.get(i);
                pw.print("*" + "'" + fila.get("lexema") + "'\n");
                for (String atributo : fila.keySet()) {
                    if (!atributo.equals("lexema") && !atributo.equals("tipoParam")) {
                        String valor = fila.get(atributo);
                        if (atributo.equals("despl")) {
                            pw.print("+" + atributo + ":" + valor + "\n");
                        } else {
                            pw.print("+" + atributo + ":'" + valor + "'\n");
                        }
                    } else if (atributo.equals("tipoParam")) {
                        String valor = fila.get(atributo);
                        if (!valor.equals("vacio")) {
                            String[] params = valor.split(",");
                            int num = params.length;
                            pw.print("+" + "numParam" + ":" + num + "\n");
                            for (int j = 0; j < params.length; j++) {
                                num = j + 1;
                                pw.print("+" + "tipoParam" + num + ":'" + params[j] + "'\n");
                            }
                        }
                        else{
                            pw.print("+" + "numParam" + ":" + 0 + "\n");
                        }
                    }
                }
            }
            for (int i = 0; i < impreso.size() - 1; i++) {
                tabla = impreso.get(i);
                int tab = i + 2;
                pw.print("#" + tab + ":\n");
                for (int j = 0; j < tabla.size(); j++) {
                    fila = tabla.get(j);
                    pw.print("*" + "'" + fila.get("lexema") + "'\n");
                    for (String atributo : fila.keySet()) {
                        if (!atributo.equals("lexema")) {
                            String valor = fila.get(atributo);
                            if (atributo.equals("despl")) {
                                pw.print("+" + atributo + ":" + valor + "\n");
                            } else {
                                pw.print("+" + atributo + ":'" + valor + "'\n");
                            }
                        }
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
