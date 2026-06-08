package com.example.inmia.admin.data;

import com.example.inmia.admin.model.AdminCompany;
import com.example.inmia.admin.model.AdminMembership;
import com.example.inmia.admin.model.AdminUser;
import com.example.inmia.models.Asesor;
import com.example.inmia.models.Proyecto;

import java.util.List;

public interface AdminRepository {

    AdminUser getUserByEmail(String email);

    AdminMembership getMembershipByUserId(String userId);

    String getCompanyIdForEmail(String email);

    AdminCompany getCompanyById(String companyId);

    List<Proyecto> getProjects(String companyId);

    Proyecto getProjectById(String companyId, String projectId);

    void addProject(String companyId, Proyecto proyecto);

    List<Asesor> getAdvisors(String companyId);

    Asesor getAdvisorById(String companyId, String advisorId);

    int getUnreadNotifications(String companyId);
}

