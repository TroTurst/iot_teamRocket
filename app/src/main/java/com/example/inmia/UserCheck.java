package com.example.inmia;

public class UserCheck {

    // Roles disponibles
    public static final String ROL_CLIENTE = "CLIENTE";
    public static final String ROL_ASESOR = "ASESOR";
    public static final String ROL_ADMIN_EMPRESA = "ADMIN_EMPRESA";
    public static final String ROL_SUPERADMIN = "SUPERADMIN";

    private static final class UserSeed {
        private final String email;
        private final String password;
        private final String role;
        private final String companyId;
        private final boolean needsCompanySetup;

        private UserSeed(String email, String password, String role, String companyId, boolean needsCompanySetup) {
            this.email = email;
            this.password = password;
            this.role = role;
            this.companyId = companyId;
            this.needsCompanySetup = needsCompanySetup;
        }
    }

    private static final UserSeed[] USUARIOS = {
            new UserSeed("daniel@gmail.com", "123456", ROL_CLIENTE, "", false),
            new UserSeed("kiara@gmail.com", "123456", ROL_ASESOR, "", false),
            new UserSeed("paul@gmail.com", "123456", ROL_ADMIN_EMPRESA, "", true),
            new UserSeed("paul2@gmail.com", "123456", ROL_ADMIN_EMPRESA, "company_01", false),
            new UserSeed("inmia@gmail.com", "123456", ROL_SUPERADMIN, "", false)
    };

    /**
     * Verifica si el email y contraseña coinciden con algún usuario.
     * Retorna el rol si es correcto, null si no coincide.
     */
    public static String getRol(String email, String password) {
        for (UserSeed usuario : USUARIOS) {
            if (usuario.email.equals(email) && usuario.password.equals(password)) {
                return usuario.role; // retorna el rol
            }
        }
        return null; // credenciales incorrectas
    }

    /**
     * Verifica solo si el email existe, sin importar la contraseña.
     * Útil para ForgotPassword más adelante.
     */
    public static boolean emailExiste(String email) {
        for (UserSeed usuario : USUARIOS) {
            if (usuario.email.equals(email)) {
                return true;
            }
        }
        return false;
    }

    public static boolean needsCompanySetup(String email) {
        for (UserSeed usuario : USUARIOS) {
            if (usuario.email.equals(email)) {
                return usuario.needsCompanySetup;
            }
        }
        return false;
    }

    public static String getCompanyId(String email) {
        for (UserSeed usuario : USUARIOS) {
            if (usuario.email.equals(email)) {
                return usuario.companyId;
            }
        }
        return "";
    }
}