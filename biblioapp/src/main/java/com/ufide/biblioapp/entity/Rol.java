package com.ufide.biblioapp.entity;

// los dos roles que maneja la app
public enum Rol {
    BIBLIOTECARIO,
    LECTOR;

    // true si el string coincide con alguno de los roles
    public static boolean esValido(String valor) {
        if (valor == null) {
            return false;
        }
        for (Rol rol : values()) {
            if (rol.name().equals(valor)) {
                return true;
            }
        }
        return false;
    }
}
