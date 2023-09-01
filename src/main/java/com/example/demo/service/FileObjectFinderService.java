
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Privacy;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import com.example.demo.service.repository.FileObjectRepository;
import java.util.List;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
TODO
- implement more search functionality

- isAllowedToView method:
	- write tests
	- How to handle if viewer Account is not logged in (is viewer null?)
*/
@Service
public class FileObjectFinderService {
	
	@Autowired
	private FileObjectRepository fileObjectRepository;
	
	@Autowired
	private RelationService relationService;
	
	public List<FileObject> getAccountsFileObjects(final Account account) {
		return fileObjectRepository.findByAccount(account);
	}
	
	public List<FileObject> list() {
		return fileObjectRepository.findAll();
	}
	
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
			throws NotImplementedException {
		
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
				final boolean viewerFollows = relationService
					.relationExists(viewer, owner, Status.FRIEND);

				final boolean ownerFollows = relationService
					.relationExists(owner, viewer, Status.FRIEND);
				
				return viewerFollows && ownerFollows;
				
			case PRIVATE:
				return false;
				
			default:
				throw new NotImplementedException(
					"Privacy " + fileObjectPrivacy + " is not yet implemented"
				);
		}
	}
}
