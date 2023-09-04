
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
	
	/**
	 * Method does not 
	 * 
	 * @param viewer
	 * @param viewed
	 * @return	true if the viewer {@link Account} is allowed to view the
	 *			viewed {@code Account}
	 * @throws jdk.jshell.spi.ExecutionControl.NotImplementedException 
	 * 
	 * @See com.example.demo.domain.Role
	 * @See com.example.demo.domain.Status
	 * @See com.example.demo.domain.Relation
	 */
	/*
	public boolean isAllowedToView(final Account viewer, final Account viewed)
			throws NotImplementedException {
		
		if (viewer == null) {
			return true;
		}
		
		final Role viewerRole = viewer.getRole();
		switch (viewerRole) {
			case USER:
				return !relationService.relationExistsAtleastOneWay(
					viewer, viewed, Status.BLOCKED
				);
				
			case ADMIN:
				return relationService.relationExists(
					viewer, viewed, Status.BLOCKED
				);
			
			default:
				throw new NotImplementedException(
					"Role " + viewerRole + " is not implemented"
				);
		}
	}
	*/
	
	/**
	 * 
	 * @param viewer
	 * @param fileObject
	 * 
	 * @return	true if {@code Account} is allowed to view the 
	 *			{@code FileObject}, false otherwise
	 * 
	 * @throws jdk.jshell.spi.ExecutionControl.NotImplementedException 
	 * 
	 * @See com.example.demo.domain.Privacy
	 */
	public boolean isAllowedToView(
			final Account viewer, final FileObject fileObject)
			throws NotImplementedException {
		
		final Privacy resourcePrivacy = fileObject.getPrivacy();
		if (isAnonymous(viewer)) {
			return handleAnonymous(resourcePrivacy);
			
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
	
	public boolean isAnonymous(final Account account) {
		return account == null;
	}
	
	private boolean handleAnonymous(final Privacy resourcePrivacy) {
		return Privacy.isAnonymousAllowedToView(resourcePrivacy);
	}
	
	private boolean handleUser(final Account viewer, final Account owner, 
			final Privacy resourcePrivacy) 
			throws NotImplementedException {
		
		final boolean isViewerTheOwnerOfTheResource
			= viewer.equals(owner);

		final boolean doesBlockExistsBetweenTheViewerAndTheOwner
			= relationService.relationExistsAtleastOneWay(
				viewer, owner, Status.BLOCKED
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
