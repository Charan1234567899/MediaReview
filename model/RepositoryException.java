package MediaReview.model;

/**
 * Our own exception for when the save file can't be read or written.
 * It extends RuntimeException, so the interface methods don't need "throws",
 * and the in-memory repository (which never touches a file) is not affected.
 * The Controller and Main catch it and tell the user in plain words.
 */
public class RepositoryException extends RuntimeException {

    public RepositoryException(String message) {
        super(message);
    }
}
