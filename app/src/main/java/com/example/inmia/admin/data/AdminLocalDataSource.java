package com.example.inmia.admin.data;

import com.example.inmia.admin.model.AdminCompany;
import com.example.inmia.admin.model.AdminMembership;
import com.example.inmia.admin.model.AdminRole;
import com.example.inmia.admin.model.AdminUser;

import java.util.Arrays;
import java.util.List;

class AdminLocalDataSource {

    private final List<AdminUser> users = Arrays.asList(
            new AdminUser("user_001", "paul2@gmail.com", "Paul Admin", "ACTIVE"),
            new AdminUser("user_002", "paul@gmail.com", "Paul Admin Setup", "ACTIVE"),
            new AdminUser("user_003", "inmia@gmail.com", "Superadmin", "ACTIVE")
    );

    private final List<AdminCompany> companies = Arrays.asList(
            new AdminCompany("company_01", "Grupo Inmobiliario INMIA", "20123456789",
                    "Av. Principal 123, Lima", "+51 999 888 777", "", "ACTIVE")
    );

    private final List<AdminMembership> memberships = Arrays.asList(
            new AdminMembership("memb_001", "user_001", "company_01", AdminRole.ADMIN_EMPRESA, "ACTIVE"),
            new AdminMembership("memb_002", "user_002", "company_01", AdminRole.ADMIN_EMPRESA, "ACTIVE"),
            new AdminMembership("memb_003", "user_003", "", AdminRole.SUPERADMIN, "ACTIVE")
    );

    AdminUser getUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        for (AdminUser user : users) {
            if (email.equalsIgnoreCase(user.getEmail())) {
                return user;
            }
        }
        return null;
    }

    AdminMembership getMembershipByUserId(String userId) {
        if (userId == null) {
            return null;
        }
        for (AdminMembership membership : memberships) {
            if (userId.equals(membership.getUserId())) {
                return membership;
            }
        }
        return null;
    }

    AdminCompany getCompanyById(String companyId) {
        if (companyId == null) {
            return null;
        }
        for (AdminCompany company : companies) {
            if (companyId.equals(company.getId())) {
                return company;
            }
        }
        return null;
    }

    int getUnreadNotifications(String companyId) {
        return 5;
    }
}

