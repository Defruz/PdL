package analizador;

import java.io.FileWriter;
import java.io.PrintWriter;

import analizador.ALexico.Token;

public class ASinSem {

    private Token token;
    private ALexico aLex;
    private FileWriter fichero;
    private PrintWriter pw;
    private TablaS tabla;
    private int despG;
    private int despL;
    private int cont;

    public ASinSem(ALexico aLex, TablaS tabla) {
        this.aLex = aLex;
        this.tabla = tabla;
        cont = 0;
    }

    public class Atrib {
        private String tipo;
        private int ancho;
        private String param;
        private String tipoRet;

        private Atrib(String tipo) {
            this.tipo = tipo;
        }

        private Atrib(String tipo, int ancho) {
            this.tipo = tipo;
            this.ancho = ancho;
        }

        private Atrib(String tipo, String param) {
            this.tipo = tipo;
            this.param = param;
        }

        public void setTipoRet(String tipoRet) {
            this.tipoRet = tipoRet;
        }

        public String getTipoRet() {
            return this.tipoRet;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getTipo() {
            return this.tipo;
        }

        public int getAncho() {
            return this.ancho;
        }

        public String getParam() {
            return this.param;
        }
    }

    private Token siguiente() {
        return aLex.aLexico();
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
            System.out.println(
                    "Error sintactico en la linea : " + aLex.getLinea() + " se encuentra el token " + token.getNombre()
                            + " cuando deberia aparecer el token " + t);
            while(!token.getNombre().equals("$")){
                token = siguiente();
            }
            aLex.imprimirTokens();
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
            System.out.println("Error sintactico en la linea " + aLex.getLinea());
            System.exit(0); // Se detiene la ejecucion del proceso (en este caso el ASintactico)
        } else {
            imprimir(3); // Solo desde p se llega a fin de fichero "$"
        }
    }

    private Atrib b() {
        Atrib b = null;
        if (nombre().equals("let")) {
            Atrib t;
            int id_pos;
            String tab;
            aLex.actualizarZonaDecl(true);
            imprimir(4);
            equipara(nombre());
            t = t();
            id_pos = token.getAtributoI();
            tab = token.getTabla();
            equipara("id");
            aLex.actualizarZonaDecl(false);
            equipara("puntComa");

            tabla.insertaTipoTS(tab, id_pos, t.getTipo());
            if (tab.equals("global")) {
                tabla.insertaDespTS(tab, id_pos, iToS(despL));
                despL += t.getAncho();
            } else {
                tabla.insertaDespTS(tab, id_pos, iToS(despG));
                despG += t.getAncho();
            }
            b = new Atrib("tipo_ok");
            b.setTipoRet("vacio");

        } else if (nombre().equals("if")) {
            Atrib e;
            Atrib g;
            imprimir(5);
            equipara(nombre());
            int linea = aLex.getLinea();
            equipara("pAbierto");
            e = e();
            equipara("pCerrado");
            g = g();
            if (!e.getTipo().equals("logico")) {
                b = new Atrib("tipo_error");
                b.setTipoRet("vacio");
                System.out.println("Error Semantico en la linea " + linea + ", la condicion del if no es de tipo logico");
            } else {
                b = new Atrib(g.getTipo());
                b.setTipoRet(g.getTipoRet());
            }

        } else if (nombre().equals("id") || nombre().equals("input") || nombre().equals("return")
                || nombre().equals("print")) {
            Atrib s;
            imprimir(6);
            s = s();
            b = new Atrib(s.getTipo());
            b.setTipoRet(s.getTipoRet());
        }
        // System.out.println(b.tipo + " " + b.getTipoRet());
        return b;
    }

    private Atrib t() {
        if (nombre().equals("int")) {
            imprimir(7);
            equipara(nombre());
            return new Atrib("entero", 1);
        } else if (nombre().equals("string")) {
            imprimir(8);
            equipara(nombre());
            return new Atrib("cadena", 64);
        } else if (nombre().equals("boolean")) {
            imprimir(9);
            equipara(nombre());
            return new Atrib("logico", 1);
        }
        return null;
    }

    private Atrib g() {
        Atrib g = null;
        if (nombre().equals("id") || nombre().equals("input") || nombre().equals("return")
                || nombre().equals("print")) {
            Atrib s;
            imprimir(10);
            s = s();
            g = new Atrib(s.getTipo());
            g.setTipoRet(s.getTipoRet());
        } else if (nombre().equals("kAbierta")) {
            Atrib c;
            Atrib o;
            imprimir(11);
            equipara(nombre());
            c = c();
            equipara("kCerrada");
            o = o();
            if (c.getTipo().equals("tipo_ok") && o.getTipo().equals("tipo_ok")) {
                g = new Atrib("tipo_ok");
                if (c.getTipoRet().equals(o.getTipoRet())) {
                    g.setTipoRet(c.getTipoRet());
                } else if (o.getTipoRet().equals("vacio")) {
                    g.setTipoRet(c.getTipoRet());
                } else if (c.getTipoRet().equals("vacio")) {
                    g.setTipoRet(o.getTipoRet());
                } else {
                    g.setTipoRet("tipo_error");
                }
            } else {
                g = new Atrib("tipo_error");
                g.setTipoRet("vacio");
            }
        }
        return g;
    }

    private Atrib c() {
        Atrib c = null;
        if (nombre().equals("let") || nombre().equals("id") || nombre().equals("if") || nombre().equals("print")
                || nombre().equals("input") || nombre().equals("return")) {
            Atrib b;
            Atrib c1;
            imprimir(12);
            b = b();
            c1 = c();
            if (b.getTipo().equals("tipo_ok") && c1.getTipo().equals("tipo_ok")) {
                c = new Atrib("tipo_ok");
                if (b.getTipoRet().equals(c1.getTipoRet())) {
                    c.setTipoRet(b.getTipoRet());
                } else if (c1.getTipoRet().equals("vacio")) {
                    c.setTipoRet(b.getTipoRet());
                } else if (b.getTipoRet().equals("vacio")) {
                    c.setTipoRet(c1.getTipoRet());
                } else {
                    c.setTipoRet("tipo_error");
                }
            } else {
                c = new Atrib("tipo_error");
                c.setTipoRet("vacio");
            }
        } else if (nombre().equals("kCerrada")) {
            imprimir(13);
            c = new Atrib("tipo_ok");
            c.setTipoRet("vacio");
        }
        return c;
    }

    private Atrib o() {
        Atrib o = null;
        if (nombre().equals("else")) {
            Atrib c;
            imprimir(14);
            equipara(nombre());
            equipara("kAbierta");
            c = c();
            o = new Atrib(c.getTipo());
            o.setTipoRet(c.getTipoRet());
            equipara("kCerrada");
        } else {
            imprimir(15);
            o = new Atrib("tipo_ok");
            o.setTipoRet("vacio");
        }
        return o;
    }

    private Atrib s() {
        Atrib s = null;
        if (nombre().equals("id")) {
            Atrib w;
            int id_pos;
            String tab;
            int linea = aLex.getLinea();
            imprimir(16);
            id_pos = token.getAtributoI();
            tab = token.getTabla();
            equipara(nombre());
            w = w();
            if (w.getTipo().equals("function")) {
                if (w.getParam().equals(tabla.buscaParam(id_pos))) {
                    s = new Atrib("tipo_ok");
                } else {
                    System.out.println("Error Semantico en la linea " + linea + ", los parametros de esta funcion no son correctos");
                    s = new Atrib("tipo_error");
                }
            } else if (tabla.buscaTipo(tab, id_pos) == null) {
                tabla.insertaTipoTS(tab, id_pos, "entero");
                if (tab.equals("global")) {
                    tabla.insertaDespTS(tab, id_pos, iToS(despL));
                    despL += 1;
                } else {
                    tabla.insertaDespTS(tab, id_pos, iToS(despG));
                    despG += 1;
                }
                s = new Atrib("tipo_ok");
            } else if (tabla.buscaTipo(tab, id_pos).equals(w.getTipo())) {
                s = new Atrib("tipo_ok");
            } else {
                s = new Atrib("tipo_error");
                System.out.println("Error Semantico en la linea " + linea + ", la asignacion no es correcta");
            }
            s.setTipoRet("vacio");
        } else if (nombre().equals("print")) {
            Atrib e;
            int linea = aLex.getLinea();
            imprimir(17);
            equipara(nombre());
            equipara("pAbierto");
            e = e();
            equipara("pCerrado");
            equipara("puntComa");
            if (e.getTipo().equals("entero") || e.getTipo().equals("cadena")) {
                s = new Atrib("tipo_ok");
            } else {
                s = new Atrib("tipo_error");
                System.out.println("Error Semantico en la linea " + linea + ", no se ha podido realizar el print");
            }
            s.setTipoRet("vacio");
        } else if (nombre().equals("input")) {
            int id_pos;
            String tab;
            int linea = aLex.getLinea();
            imprimir(18);
            equipara(nombre());
            equipara("pAbierto");
            id_pos = token.getAtributoI();
            tab = token.getTabla();
            equipara("id");
            equipara("pCerrado");
            equipara("puntComa");
            if (tabla.buscaTipo(tab, id_pos) != null && (tabla.buscaTipo(tab, id_pos).equals("entero")
                    || tabla.buscaTipo(tab, id_pos).equals("cadena"))) {
                s = new Atrib("tipo_ok");
            } else if (tabla.buscaTipo(tab, id_pos) != null && tabla.buscaTipo(tab, id_pos).equals("logico")) {
                s = new Atrib("tipo_error");
                System.out.println(
                        "Error Semantico en la linea " + linea + ", no se puede realizar el input de una variable logica");
            } else {
                tabla.insertaTipoTS(tab, id_pos, "entero");
                if (!tab.equals("global")) {
                    tabla.insertaDespTS(tab, id_pos, iToS(despL));
                    despL += 1;
                } else {
                    tabla.insertaDespTS(tab, id_pos, iToS(despG));
                    despG += 1;
                }
                s = new Atrib("tipo_ok");
            }
            s.setTipoRet("vacio");
        } else if (nombre().equals("return")) {
            Atrib x;
            int linea = aLex.getLinea();
            imprimir(19);
            equipara(nombre());
            x = x();
            equipara("puntComa");
            if (!x.getTipo().equals("tipo_error")) {
                s = new Atrib("tipo_ok");
            } else {
                s = new Atrib("tipo_error");
                System.out.println("Error Semantico en la linea " + linea + ", la expresion del return no es valida");
            }
            s.setTipoRet(x.getTipo());
        }
        return s;
    }

    private Atrib w() {
        Atrib w = null;
        if (nombre().equals("asigResta")) {
            Atrib e;
            imprimir(20);
            equipara(nombre());
            e = e();
            equipara("puntComa");
            w = new Atrib(e.getTipo(), "vacio");
        } else if (nombre().equals("asig")) {
            Atrib e;
            imprimir(21);
            equipara(nombre());
            e = e();
            equipara("puntComa");
            w = new Atrib(e.getTipo(), "vacio");
        } else if (nombre().equals("pAbierto")) {
            Atrib l;
            imprimir(22);
            equipara(nombre());
            l = l();
            equipara("pCerrado");
            equipara("puntComa");
            w = new Atrib("function", l.getTipo());
        }
        return w;
    }

    private Atrib x() {
        Atrib x = null;
        if (nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt")
                || nombre().equals("cadena")) {
            Atrib e;
            imprimir(23);
            e = e();
            x = new Atrib(e.getTipo());
        } else if (nombre().equals("puntComa")) {
            imprimir(24);
            x = new Atrib("vacio");
        }
        return x;
    }

    private Atrib l() {
        Atrib l = null;
        if (nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt")
                || nombre().equals("cadena")) {
            Atrib e;
            Atrib q;
            imprimir(25);
            e = e();
            q = q();
            if (q.getTipo().equals("vacio")) {
                l = new Atrib(e.getTipo());
            } else {
                l = new Atrib(e.getTipo() + "," + q.getTipo());
            }
        } else if (nombre().equals("pCerrado")) {
            imprimir(26);
            l = new Atrib("vacio");
        }
        return l;
    }

    private Atrib q() {
        Atrib q = null;
        if (nombre().equals("coma")) {
            Atrib e;
            Atrib q1;
            imprimir(27);
            equipara(nombre());
            e = e();
            q1 = q();
            if (q1.getTipo().equals("vacio")) {
                q = new Atrib(e.getTipo());
            } else {
                q = new Atrib(e.getTipo() + "," + q1.getTipo());
            }
        } else if (nombre().equals("pCerrado")) {
            imprimir(28);
            q = new Atrib("vacio");
        }
        return q;
    }

    private Atrib f() {
        Atrib f = null;
        if (nombre().equals("function")) {
            Atrib h;
            Atrib a;
            Atrib c;
            int id_pos;
            String tab;
            int linea = aLex.getLinea();
            imprimir(29);
            equipara(nombre());
            id_pos = token.getAtributoI();
            tab = token.getTabla();
            equipara("id");
            tabla.setTSActual();
            despL = 0;
            tabla.insertaEtTS(id_pos);
            h = h();
            equipara("pAbierto");
            tabla.insertaTipoRet(id_pos, h.getTipo());
            aLex.actualizarZonaDecl(true);
            a = a();
            equipara("pCerrado");
            aLex.actualizarZonaDecl(false);
            equipara("kAbierta");

            tabla.insertaTipoParam(id_pos, a.getParam());
            for (int i = 0; i < cont; i++) {
                tabla.insertaDespTS(tab, i + 1, iToS(despL));
                despL += tabla.anchoTipo(tabla.buscaTipo(tab, i + 1));
            }
            cont = 0;

            c = c();
            equipara("kCerrada");
            if (!c.getTipoRet().equals(h.getTipo())) {
                System.out.println("Error Semantico en el retorno de la funcion de la linea " + linea);
            }
            if (c.getTipo().equals("tipo_error")) {
                System.out.println("Error Semantico en el cuerpo de la funcion de la linea " + linea);
            }
            tabla.destruyeTS();
        }
        return f;
    }

    private Atrib h() {
        Atrib h = null;
        if (nombre().equals("int") || nombre().equals("string") || nombre().equals("boolean")) {
            Atrib t;
            imprimir(30);
            t = t();
            h = new Atrib(t.getTipo());
        } else if (nombre().equals("pAbierto")) {
            imprimir(31);
            h = new Atrib("vacio");
        }
        return h;
    }

    private Atrib a() {
        Atrib a = null;
        if (nombre().equals("int") || nombre().equals("string") || nombre().equals("boolean")) {
            Atrib t;
            Atrib k;
            int id_pos;
            String tab;
            imprimir(32);
            t = t();
            id_pos = token.getAtributoI();
            tab = token.getTabla();
            equipara("id");
            k = k();
            tabla.insertaTipoTS(tab, id_pos, t.getTipo());
            cont++;
            // tabla.insertaDespTS(id_pos, iToS(despL));
            // despL += t.getAncho();
            if (k.param.equals("vacio")) {
                a = new Atrib(k.getTipo(), t.getTipo());
            } else {
                a = new Atrib(k.getTipo(), t.getTipo() + "," + k.param);
            }
        } else if (nombre().equals("pCerrado")) {
            imprimir(33);
            a = new Atrib("tipo_ok", "vacio");
        }
        return a;
    }

    private Atrib k() {
        Atrib k = null;
        if (nombre().equals("coma")) {
            Atrib t;
            Atrib k1;
            int id_pos;
            String tab;
            imprimir(34);
            equipara(nombre());
            t = t();
            id_pos = token.getAtributoI();
            tab = token.getTabla();
            equipara("id");
            k1 = k();
            tabla.insertaTipoTS(tab, id_pos, t.getTipo());
            cont++;
            // tabla.insertaDespTS(id_pos, iToS(despL));
            // despL += t.getAncho();
            if (k1.param.equals("vacio")) {
                k = new Atrib(k1.getTipo(), t.getTipo());
            } else {
                k = new Atrib(k1.getTipo(), t.getTipo() + "," + k1.param);
            }
        } else if (nombre().equals("pCerrado")) {
            imprimir(35);
            k = new Atrib("tipo_ok", "vacio");
        }
        return k;
    }

    private Atrib e() {
        Atrib e = null;
        if (nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt")
                || nombre().equals("cadena")) {
            Atrib r;
            Atrib e_;
            int linea = aLex.getLinea();
            imprimir(36);
            r = r();
            e_ = e_();
            if (r.getTipo().equals("logico") && e_.getTipo().equals("logico")) {
                e = new Atrib("logico");
            } else if (e_.getTipo().equals("vacio")) {
                e = new Atrib(r.getTipo());
            } else {
                e = new Atrib("tipo_error");
            }
            if (e.getTipo().equals("tipo_error")){
                System.out.println("Error Semantico en la linea " + linea + ", la expresion no es correcta");
            }
        }
        return e;
    }

    private Atrib e_() {
        Atrib e_ = null;
        if (nombre().equals("and")) {
            Atrib r;
            Atrib e1;
            imprimir(37);
            equipara(nombre());
            r = r();
            e1 = e_();
            if (r.getTipo().equals("logico") && !e1.getTipo().equals("tipo_error")) {
                e_ = new Atrib("logico");
            } else {
                e_ = new Atrib("tipo_error");
            }
        } else if (nombre().equals("puntComa") || nombre().equals("pCerrado") || nombre().equals("coma")) {
            imprimir(38);
            e_ = new Atrib("vacio");
        }
        return e_;
    }

    private Atrib r() {
        Atrib r = null;
        if (nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt")
                || nombre().equals("cadena")) {
            Atrib u;
            Atrib r_;
            imprimir(39);
            u = u();
            r_ = r_();
            if (r_.getTipo().equals("logico")) {
                r = new Atrib("logico");
            } else if (r_.getTipo().equals("vacio")) {
                r = new Atrib(u.getTipo());
            } else {
                r = new Atrib("tipo_error");
            }
        }
        return r;
    }

    private Atrib r_() {
        Atrib r_ = null;
        if (nombre().equals("menor")) {
            Atrib u;
            Atrib r1;
            imprimir(40);
            equipara(nombre());
            u = u();
            r1 = r_();
            if (!u.getTipo().equals("tipo_error") && !r1.getTipo().equals("tipo_error")) {
                r_ = new Atrib("logico");
            } else {
                r_ = new Atrib("tipo_error");
            }
        } else if (nombre().equals("mayor")) {
            Atrib u;
            Atrib r1;
            imprimir(41);
            equipara(nombre());
            u = u();
            r1 = r_();
            if (!u.getTipo().equals("tipo_error") && !r1.getTipo().equals("tipo_error")) {
                r_ = new Atrib("logico");
            } else {
                r_ = new Atrib("tipo_error");
            }
        } else if (nombre().equals("puntComa") || nombre().equals("pCerrado") || nombre().equals("coma")
                || nombre().equals("and")) {
            imprimir(42);
            r_ = new Atrib("vacio");
        }
        return r_;
    }

    private Atrib u() {
        Atrib u = null;
        if (nombre().equals("id") || nombre().equals("pAbierto") || nombre().equals("numEnt")
                || nombre().equals("cadena")) {
            Atrib v;
            Atrib u_;
            imprimir(43);
            v = v();
            u_ = u_();
            if (u_.getTipo().equals("entero") && v.getTipo().equals("entero")) {
                u = new Atrib("entero");
            } else if (u_.getTipo().equals("vacio")) {
                u = new Atrib(v.getTipo());
            } else {
                u = new Atrib("tipo_error");
            }
        }
        return u;
    }

    private Atrib u_() {
        Atrib u_ = null;
        if (nombre().equals("suma")) {
            Atrib v;
            Atrib u1;
            imprimir(44);
            equipara(nombre());
            v = v();
            u1 = u_(); 
            if (v.getTipo().equals("entero") && u1.getTipo().equals("entero") 
                    || v.getTipo().equals("entero") && u1.getTipo().equals("vacio")) {
                u_ = new Atrib("entero");
            } else {
                u_ = new Atrib("tipo_error");
            }
        } else if (nombre().equals("resta")) {
            Atrib v;
            Atrib u1;
            imprimir(45);
            equipara(nombre());
            v = v();
            u1 = u_();
            if (v.getTipo().equals("entero") && u1.getTipo().equals("entero") 
            || v.getTipo().equals("entero") && u1.getTipo().equals("vacio")) {
                u_ = new Atrib("entero");
            } else {
                u_ = new Atrib("tipo_error");
            }
        } else if (nombre().equals("puntComa") || nombre().equals("pCerrado") || nombre().equals("coma")
                || nombre().equals("and") || nombre().equals("mayor") || nombre().equals("menor")) {
            imprimir(46);
            u_ = new Atrib("vacio");
        }
        return u_;
    }

    private Atrib v() {
        Atrib v = null;
        if (nombre().equals("id")) {
            Atrib d;
            int id_pos;
            String tab;
            int linea = aLex.getLinea();
            imprimir(47);
            id_pos = token.getAtributoI();
            tab = token.getTabla();
            equipara(nombre());
            d = d();
            if (d.getTipo().equals("function")) {
                if (d.getParam().equals(tabla.buscaParam(id_pos))) {
                    v = new Atrib(tabla.buscaTipoRet(id_pos), tabla.anchoTipo(tabla.buscaTipoRet(id_pos)));
                } else {
                    System.out.println("Error Semantico en la linea " + linea + ", los parametros de esta funcion no son correctos");
                    v = new Atrib("tipo_error", 0);
                }
            } else if (tabla.buscaTipo(tab, id_pos) == null) {
                tabla.insertaTipoTS(tab, id_pos, "entero");
                if (tab.equals("global")) {
                    tabla.insertaDespTS(tab, id_pos, iToS(despL));
                    despL += 1;
                } else {
                    tabla.insertaDespTS(tab, id_pos, iToS(despG));
                    despG += 1;
                }
                v = new Atrib("entero", 1);
            } else {
                String tipo = tabla.buscaTipo(tab, id_pos);
                v = new Atrib(tipo, tabla.anchoTipo(tipo));
            }
        } else if (nombre().equals("pAbierto")) {
            Atrib e;
            imprimir(48);
            equipara(nombre());
            e = e();
            equipara("pCerrado");
            v = new Atrib(e.getTipo(), 0);
        } else if (nombre().equals("numEnt")) {
            imprimir(49);
            equipara(nombre());
            v = new Atrib("entero", 1);
        } else if (nombre().equals("cadena")) {
            imprimir(50);
            equipara(nombre());
            v = new Atrib("cadena", 64);
        }
        return v;
    }

    private Atrib d() {
        Atrib d = null;
        if (nombre().equals("pAbierto")) {
            Atrib l;
            imprimir(51);
            equipara(nombre());
            l = l();
            equipara("pCerrado");
            d = new Atrib("function", l.getTipo());
        } else if (nombre().equals("puntComa") || nombre().equals("pCerrado") || nombre().equals("coma")
                || nombre().equals("and") || nombre().equals("mayor") || nombre().equals("menor")
                || nombre().equals("suma") || nombre().equals("resta")) {
            imprimir(52);
            d = new Atrib("vacio", "vacio");
        }
        return d;
    }

    public void aSintactico() {
        crearParse();
        despG = 0;
        despL = 0;
        token = siguiente(); // Primer token
        p();
        tabla.destruyeTS();
        tabla.imprimirTabla();
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

    private String iToS(int i) {
        return "" + i;
    }

}
