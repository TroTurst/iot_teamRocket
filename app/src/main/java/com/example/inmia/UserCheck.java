package com.example.inmia;

public class UserCheck {

    // Roles disponibles
    public static final String ROL_CLIENTE    = "cliente";
    public static final String ROL_ASESOR     = "asesor";
    public static final String ROL_ADMIN      = "admin";
    public static final String ROL_SUPERADMIN = "superadmin";

    public static final String ROL_ADMIN1      = "admin1";

    // Usuarios hardcodeados — temporales hasta Firebase
    private static final String[][] USUARIOS = {
            // { email, contraseña, rol }
            { "daniel@gmail.com",    "123456", ROL_CLIENTE    },
            { "kiara@gmail.com",     "123456", ROL_ASESOR     },
            { "paul@gmail.com",      "123456", ROL_ADMIN      },
            { "superadmin@gmail.com", "123456", ROL_SUPERADMIN },
            { "paul2@gmail.com",      "123456", ROL_ADMIN1 }
    };

    /**
     * Verifica si el email y contraseña coinciden con algún usuario.
     * Retorna el rol si es correcto, null si no coincide.
     */
    public static String getRol(String email, String password) {
        for (String[] usuario : USUARIOS) {
            if (usuario[0].equals(email) && usuario[1].equals(password)) {
                return usuario[2]; // retorna el rol
            }
        }
        return null; // credenciales incorrectas
    }

    /**
     * Verifica solo si el email existe, sin importar la contraseña.
     * Útil para ForgotPassword más adelante.
     */
    public static boolean emailExiste(String email) {
        for (String[] usuario : USUARIOS) {
            if (usuario[0].equals(email)) {
                return true;
            }
        }
        return false;
    }
}