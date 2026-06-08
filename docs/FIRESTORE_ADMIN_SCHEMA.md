# Firestore - Admin multiempresa (borrador)

## Objetivo
Este esquema deja listo el rol ADMIN_EMPRESA (y SUPERADMIN) para consumir datos dinamicos desde Firestore.
Aun no se conecta Firebase, pero las colecciones y campos ya estan definidos.

## Colecciones principales

### users/{userId}
- email: string
- displayName: string
- status: ACTIVE | INACTIVE
- createdAt: timestamp

### companies/{companyId}
- name: string
- ruc: string
- address: string
- phone: string
- logoUrl: string
- status: ACTIVE | INACTIVE

### memberships/{membershipId}
Relacion usuario-empresa-rol (multiempresa).
- userId: string
- companyId: string
- role: SUPERADMIN | ADMIN_EMPRESA | ASESOR | CLIENTE
- status: ACTIVE | INACTIVE

## Subcolecciones por empresa

### companies/{companyId}/projects/{projectId}
- name: string
- location: string
- priceFrom: number
- badge: string
- imageUrl: string
- status: ACTIVE | DRAFT
- createdAt: timestamp

### companies/{companyId}/advisors/{advisorId}
- name: string
- email: string
- phone: string
- photoUrl: string
- status: ACTIVE | INACTIVE

### companies/{companyId}/reports/{reportId}
- type: string
- createdAt: timestamp
- data: map

## Indices recomendados
- memberships: userId + status
- memberships: companyId + role
- companies/{companyId}/projects: status + createdAt

## Reglas (borrador)
- SUPERADMIN puede leer todo.
- ADMIN_EMPRESA solo puede leer documentos con su companyId.
- ASESOR y CLIENTE quedan fuera del scope de admin por ahora.

## Datos semilla para pruebas
- user: paul2@gmail.com -> role ADMIN_EMPRESA -> companyId company_01
- user: inmia@gmail.com -> role SUPERADMIN

