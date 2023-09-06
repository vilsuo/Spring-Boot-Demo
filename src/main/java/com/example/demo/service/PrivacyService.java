
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Privacy;
import com.example.demo.domain.Role;
import static com.example.demo.domain.Role.ADMIN;
import static com.example.demo.domain.Role.USER;
import com.example.demo.domain.Status;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivacyService {
	
	@Autowired
	private RelationFinderService relationFinderService;
	
	public boolean isBlockedFromViewingAllResourcesFromAccount(
			final Account viewer, final Account viewed) 
			throws NotImplementedException {
		
		if (Role.isAnonymous(viewer)) { return false; }
		
		final Role viewerRole = viewer.getRole();
		switch (viewerRole) {
			case USER:
				return relationFinderService.relationExistsAtleastOneWay(
					viewer, viewed, Status.BLOCKED
				);
			case ADMIN:
				return relationFinderService.relationExists(
					viewer, viewed, Status.BLOCKED
				);
			default:
				throw new NotImplementedException(
					"Role " + viewerRole + " is not implemented"
				);
		}
	}
	
	/**
	 * 
	 * @param viewer
	 * @param fileObject
	 * @return	true if the viewer {@code Account} is allowed to view the 
	 *			{@code FileObject}, false otherwise
	 * @throws	jdk.jshell.spi.ExecutionControl.NotImplementedException if the
	 *			{@link Role} of the viewer {@code Account} it not implemented
	 * 
	 * @See com.example.demo.domain.Privacy
	 * @See com.example.demo.domain.Relation
	 */
	public boolean isViewerAllowedToViewFileObject(
			final Account viewer, final FileObject fileObject)
			throws NotImplementedException {

		return isViewerAllowedToViewResource(
			viewer, fileObject.getAccount(), fileObject.getPrivacy()
		);
	}
	
	public boolean isViewerAllowedToViewResource(final Account viewer, 
			final Account resourceOwner, final Privacy resourcePrivacy)
			throws NotImplementedException {
		
		if (Role.isAnonymous(viewer)) {
			return isAnonymousAllowedToViewResource(resourcePrivacy);
			
		} else {
			if (viewer == null) {
				throw new IllegalStateException(
					"Viewer is not anonymous but viewer is null!"
				);
			}
			final Role viewerRole = viewer.getRole();
			
			switch (viewerRole) {
				case USER:
					return isUserAllowedToViewResource(
						viewer, resourceOwner, resourcePrivacy
					);
					
				case ADMIN:
					return isAdminAllowedToViewResource(viewer, resourceOwner);
					
				default:
					throw new NotImplementedException(
						"Role " + viewerRole + " is not implemented"
					);
			}
		}
	}
	
	private boolean isAnonymousAllowedToViewResource(
			final Privacy resourcePrivacy) {
		
		return resourcePrivacy == Privacy.ALL;
	}
	
	private boolean isUserAllowedToViewResource(final Account viewer,
			final Account resourceOwner, final Privacy resourcePrivacy)
			throws NotImplementedException {
		
		if (resourceOwner.equals(viewer)) { return true; }
		
		final boolean doesBlockExistsBetweenTheViewerAndTheOwner
			= relationFinderService.relationExistsAtleastOneWay(
				viewer, resourceOwner, Status.BLOCKED
			);
		if (doesBlockExistsBetweenTheViewerAndTheOwner) {
			return false;
		}
		
		switch (resourcePrivacy) {
			case ALL:
				return true;
			
			case FRIENDS:
				return relationFinderService.relationExistsBothWays(
					viewer, resourceOwner, Status.FRIEND
				);
				
			case PRIVATE:
				return false;
			
			default:
				throw new NotImplementedException(
					"Privacy " + resourcePrivacy + " is not implemented"
				);
		}
	}
	
	private boolean isAdminAllowedToViewResource(
			final Account viewer, final Account resourceOwner) {
		
		if (resourceOwner.equals(viewer)) { return true; }
		
		return !relationFinderService
			.relationExists(viewer, resourceOwner, Status.BLOCKED);
	}
}
