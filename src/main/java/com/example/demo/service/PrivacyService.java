
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Privacy;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivacyService {
	
	@Autowired
	private RelationService relationService;
	
	public boolean isAllowedToViewFileObject(
			final Account viewer, final FileObject fileObject)
			throws NotImplementedException {
		
		final Privacy resourcePrivacy = fileObject.getPrivacy();
		if (viewer == null) {
			return resourcePrivacy == Privacy.ALL;
			
		} else {
			final Role viewerRole = viewer.getRole();
			final Account owner = fileObject.getAccount();
			
			switch (viewerRole) {
				case USER:
					return handleUser(viewer, owner, resourcePrivacy);
					
				case ADMIN:
					return handleAdmin(viewer, owner);
				
				default:
					throw new NotImplementedException(
						"Role " + viewerRole + " is not implemented"
					);
			}
		}
	}
	
	private boolean handleUser(final Account viewer, final Account owner, 
			final Privacy resourcePrivacy) 
			throws NotImplementedException {
		
		final boolean isViewerTheOwnerOfTheResource
			= viewer.equals(owner);

		final boolean doesBlockExistsBetweenTheViewerAndTheOwner
			= relationService.relationExistsAtleastOneWay(
				viewer, owner, Status.FRIEND
			);
		
		final boolean areTheViewerAndTheOwnerMutualFriends
			= relationService.relationExistsBothWays(
				viewer, owner, Status.FRIEND
			);
			
		return Privacy.isUserAllowedToView(
			isViewerTheOwnerOfTheResource,
			resourcePrivacy,
			doesBlockExistsBetweenTheViewerAndTheOwner,
			areTheViewerAndTheOwnerMutualFriends
		);
	}
	
	private boolean handleAdmin(final Account viewer, final Account owner) {
		final boolean hasAdminBlockedTheOwnerOfTheResource
			= relationService
				.relationExists(viewer, owner, Status.BLOCKED);
		
		return Privacy.isAdminAllowedToView(
			hasAdminBlockedTheOwnerOfTheResource
		);
	}
}
