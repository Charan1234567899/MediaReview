package MediaReview.model;

import java.util.ArrayList;

/**
 * Stores reviews in an ArrayList while the app is open.
 * Nothing is saved to disk here - PersistantMediaReviewRepository adds that part.
 * This is the primary class for the future GUI. The PersistantMediaReviewRepository class
 * will copy the information within the ArrayList of the InMemoryMediaReviewrepository.
 * Both classes have the methods that are in the MediaReviewRepository Interface.
 *
 * @author Ethan Porter
 */
public class InMemoryMediaReviewRepository implements MediaReviewRepository {

    private ArrayList<MediaReview> reviews;
    private static int nextId = 1;

    public InMemoryMediaReviewRepository() {
        /*Unit 9: Static, the static variables ensures that the nextId is associated with the class
        which prevents multiple objects from having the same ID number. */
        reviews = new ArrayList<MediaReview>();
    }

    @Override
    public ArrayList<MediaReview> findAll() {
        // Return a copy so other classes can't change our list directly Unit 6 (encapsulation)
        return new ArrayList<MediaReview>(reviews);
    }

    @Override
    public MediaReview findById(int id) {
        for (MediaReview review : reviews) {
            if (review.getId() == id) {
                return review;
            }
        }
        return null; // no review has that ID
    }

    @Override
    public MediaReview save(MediaReview review) {
        if (review.getId() == 0) {
            // ID 0 means brand-new record, so give it the next real ID
            review.setId(nextId);
            nextId++;
            reviews.add(review);
            return review;
        } else {
            // Existing record: replace the old version that has the same ID
            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getId() == review.getId()) {
                    reviews.set(i, review);
                    return review;
                }
            }
            // No review has this ID, so there is nothing to update.
            // Reject it instead of adding it, so bad input never changes what is stored.
            throw new IllegalArgumentException("No review with ID " + review.getId() + " to update.");
        }
    }

    // Used only by PersistantMediaReviewRepository when it loads reviews from the file.
    // No access modifier = package-private: only classes in the model package can call it.
    // It adds a review that already has an ID and keeps nextId ahead of it.
    void addLoadedReview(MediaReview review) {
        reviews.add(review);
        if (review.getId() >= nextId) {
            nextId = review.getId() + 1;
        }
    }

    @Override
    public void deleteById(int id) {
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId() == id) {
                reviews.remove(i);
                return;
            }
        }
    }
}
