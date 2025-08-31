package com.narvee.ats.auth.util;

import java.util.ArrayList;
import java.util.List;

import com.narvee.ats.auth.entity.TmsRoles;

public class TmsRolesInitializer {

	public static List<TmsRoles> createDefaultRoles(long addedByUserId, long adminId) {
		List<TmsRoles> roles = new ArrayList<>();
      
		
		roles.add(createRole("Super Admin", "System super administrator with full access", addedByUserId, adminId));
		roles.add(createRole("Admin", "System administrator with full access", addedByUserId, adminId));
		roles.add(createRole("Project Manager", "Oversees project execution and team coordination", addedByUserId,
				adminId));
		roles.add(createRole("Team Lead", "Leads a team and manages task allocation", addedByUserId, adminId));
		roles.add(createRole("Team Member", "Performs assigned tasks in the project", addedByUserId, adminId));
		roles.add(createRole("Client", "Receives project deliverables and gives feedback", addedByUserId, adminId));
		roles.add(createRole("Observer", "Has read-only access to project progress", addedByUserId, adminId));

		return roles;
	}

	private static TmsRoles createRole(String name, String description, long addedBy, long adminId) {
		TmsRoles role = new TmsRoles();
		role.setRolename(name);
		role.setDescription(description);
		role.setStatus("Active");
		role.setAddedby(addedBy);
		role.setUpdatedby(addedBy); // can also be 0 if not updated yet
		role.setAdminId(adminId);

		return role;
	}
}
