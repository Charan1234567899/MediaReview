package MediaReview.Model;

import java.util.ArrayList;

/**
 * Stores reviews in an ArrayList while the app is open.
 * Nothing is saved to disk here - PersistantMediaReviewRepository adds that part.
 */
public class InMemoryMediaReviewRepository implements MediaReviewRepository {

    // protected so the Persistant subclass can load file data into the same list
    protected ArrayList<MediaReview> reviews;
    protected int nextId;

    public InMemoryMediaReviewRepository() {
        reviews = new ArrayList<MediaReview>();
        nextId = 1;
    }

    @Override
    public ArrayList<MediaReview> findAll() {
        // Return a copy so other classes can't change our list directly (encapsulation)
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
            // ID was not in the list yet, so add it and keep nextId ahead of it
            reviews.add(review);
            if (review.getId() >= nextId) {
                nextId = review.getId() + 1;
            }
            return review;
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
