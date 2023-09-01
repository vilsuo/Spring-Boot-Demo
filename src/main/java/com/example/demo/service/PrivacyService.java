
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Privacy;
import static com.example.demo.domain.Privacy.ALL;
import static com.example.demo.domain.Privacy.FRIENDS;
import static com.example.demo.domain.Privacy.PRIVATE;
import static com.example.demo.domain.Privacy.SIGNED;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import jdk.jshell.spi.ExecutionControl;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
TODO
- isAllowedToView method:
	- write tests
	- How to handle if viewer Account is not logged in (is viewer null?)
*/
@Service
public class PrivacyService {
	
	@Autowired
	private RelationService relationService;
	
	/**
	 * 
	 * @param viewer
	 * 
	 * @param fileObject
	 * @return 
	 * @throws jdk.jshell.spi.ExecutionControl.NotImplementedException 
	 * 
	 * @see com.example.demo.domain.Privacy
	 */
	public boolean isAllowedToView(
			final Account viewer, final FileObject fileObject)
			throws ExecutionControl.NotImplementedException {
		
		/*
		if viewer Account is not logged in (viewer == null), the viewer can only
		see the FileObject if the privacy of the FileObject is Privacy.ALL
		*/
		if (viewer == null) {
			return fileObject.getPrivacy() == Privacy.ALL;
		}
		
		/*
		if viwer Account has Role.ADMIN, then the viewer can see all FileObjets
		reagardless of Privacy option
		*/
		if (viewer.getRole() == Role.ADMIN) {
			return true;
		}
		
		final Account owner = fileObject.getAccount();
		
		// every Account can see its own FileObjects
		if (viewer.equals(owner)) {
			return true;
		}
		
		final boolean viewerHasBlocked = relationService
			.relationExists(viewer, owner, Status.BLOCKED);
		
		final boolean ownerHasBlocked = relationService
			.relationExists(owner, viewer, Status.BLOCKED);
		
		if (viewerHasBlocked || ownerHasBlocked) {
			return false;
		}
		
		final Privacy fileObjectPrivacy = fileObject.getPrivacy();
		switch (fileObjectPrivacy) {
			case ALL:
				return true;
				
			case SIGNED:
				return viewer != null;
				
			case FRIENDS:
				/*
				viewer Account and owner Account of the FileObject are MUTUAL
				Status.FRIEND s
				*/
				return relationService.mutualRelationExists(
					viewer, owner, Status.FRIEND
				);
				
			case PRIVATE:
				return false;
			
			default:
				throw new NotImplementedException(
					"Privacy " + fileObjectPrivacy + " is not yet implemented"
				);
		}
	}
}
