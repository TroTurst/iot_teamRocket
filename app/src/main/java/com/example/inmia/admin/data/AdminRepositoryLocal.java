package com.example.inmia.admin.data;

import com.example.inmia.admin.model.AdminCompany;
import com.example.inmia.admin.model.AdminMembership;
import com.example.inmia.admin.model.AdminUser;
import com.example.inmia.models.Asesor;
import com.example.inmia.models.Proyecto;

import java.util.List;

public class AdminRepositoryLocal implements AdminRepository {

    private final AdminLocalDataSource localDataSource;

    public AdminRepositoryLocal(AdminLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override
    public AdminUser getUserByEmail(String email) {
        return localDataSource.getUserByEmail(email);
    }

    @Override
    public AdminMembership getMembershipByUserId(String userId) {
        return localDataSource.getMembershipByUserId(userId);
    }

    @Override
    public String getCompanyIdForEmail(String email) {
        AdminUser user = getUserByEmail(email);
        if (user == null) {
            return AdminSessionDefaults.DEFAULT_COMPANY_ID;
        }
        AdminMembership membership = getMembershipByUserId(user.getId());
        if (membership == null || membership.getCompanyId() == null || membership.getCompanyId().isEmpty()) {
            return AdminSessionDefaults.DEFAULT_COMPANY_ID;
        }
        return membership.getCompanyId();
    }

    @Override
    public AdminCompany getCompanyById(String companyId) {
        return localDataSource.getCompanyById(companyId);
    }

    @Override
    public List<Proyecto> getProjects(String companyId) {
        return AdminProyectoRepositoryMock.getProyectos();
    }

    @Override
    public Proyecto getProjectById(String companyId, String projectId) {
        return AdminProyectoRepositoryMock.getProyectoById(projectId);
    }

    @Override
    public void addProject(String companyId, Proyecto proyecto) {
        AdminProyectoRepositoryMock.addProyecto(proyecto);
    }

    @Override
    public List<Asesor> getAdvisors(String companyId) {
        return AdminAsesorRepositoryMock.getAsesores();
    }

    @Override
    public Asesor getAdvisorById(String companyId, String advisorId) {
        return AdminAsesorRepositoryMock.getAsesorById(advisorId);
    }

    @Override
    public int getUnreadNotifications(String companyId) {
        return localDataSource.getUnreadNotifications(companyId);
    }
}

