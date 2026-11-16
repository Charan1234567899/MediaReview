package MediaReview;

import MediaReview.model.InMemoryMediaReviewRepository;
import MediaReview.model.MediaReview;
import MediaReview.model.MediaReviewRepository;
import java.util.ArrayList;

/**
 * This is a simple console application that demonstrantes the MediaReviewRepository interface and its InMemoryMediaReviewRepository implementation.
 * It creates, lists, updates and deletes reviews using the in-memory
 * repository, prints the result of each step, and then tries some bad input.
 */
public class ConsoleMain {

    public static void main(String[] args) {
        // Declared as the interface type, holding the in-memory implementation (polymorphism)
        MediaReviewRepository repository = new InMemoryMediaReviewRepository();

        // ---------- CREATE ----------
        System.out.println("===== CREATE =====");
        // ID 0 means "new", so the repository gives each review its real ID
        MediaReview dune = repository.save(new MediaReview(0, "Dune", 4.5,
                "Denis Villeneuve", "Sci-fi", "Amazing visuals and sound.", true));
        System.out.println("Saved: " + dune);

        MediaReview interstellar = repository.save(new MediaReview(0, "Interstellar", 5.0,
                "Christopher Nolan", "Sci-fi", "The docking scene!", true));
        System.out.println("Saved: " + interstellar);

        MediaReview superbad = repository.save(new MediaReview(0, "Superbad", 3.5,
                "Greg Mottola", "Comedy", "Funny with friends.", false));
        System.out.println("Saved: " + superbad);

        // ---------- LIST ----------
        System.out.println();
        System.out.println("===== LIST =====");
        printAll(repository);

        // ---------- UPDATE ----------
        System.out.println();
        System.out.println("===== UPDATE =====");
        MediaReview toUpdate = repository.findById(2);
        if (toUpdate != null) {
            toUpdate.setScore(4.0);
            toUpdate.setFavorite(false);
            toUpdate.setNotes("Rewatched it, still good but long.");
            repository.save(toUpdate); // same ID, so this updates instead of adding
            System.out.println("Updated: " + repository.findById(2));
        }
        System.out.println("Number of reviews is still " + repository.findAll().size()
                + " (update did not add a new row).");

        // ---------- DELETE ----------
        System.out.println();
        System.out.println("===== DELETE =====");
        repository.deleteById(3);
        System.out.println("Deleted review #3.");
        printAll(repository);

        // ---------- BAD INPUT ----------
        System.out.println();
        System.out.println("===== BAD INPUT =====");

        // 1. Score below 0 is rejected by the entity's setter
        try {
            dune.setScore(-1);
            System.out.println("ERROR: a score of -1 was accepted.");
        } catch (IllegalArgumentException e) {
            System.out.println("Score of -1 rejected: " + e.getMessage());
        }

        // 2. Score above 5 is rejected, even when creating a new review
        try {
            repository.save(new MediaReview(0, "Bad Movie", 7, "Nobody", "Other", "", false));
            System.out.println("ERROR: a score of 7 was accepted.");
        } catch (IllegalArgumentException e) {
            System.out.println("Score of 7 rejected: " + e.getMessage());
        }

        // 3. A score typed as letters can't be turned into a number
        String typedScore = "abc";
        try {
            double score = Double.parseDouble(typedScore);
            dune.setScore(score);
            System.out.println("ERROR: a score of \"abc\" was accepted.");
        } catch (NumberFormatException e) {
            System.out.println("Score of \"" + typedScore + "\" rejected: it is not a number.");
        }

        // 4. An empty score can't be turned into a number either
        typedScore = "";
        try {
            double score = Double.parseDouble(typedScore);
            dune.setScore(score);
            System.out.println("ERROR: an empty score was accepted.");
        } catch (NumberFormatException e) {
            System.out.println("Empty score rejected: please enter a number.");
        }

        // 5. Finding, updating or deleting an ID that does not exist
        int missingId = 99;
        if (repository.findById(missingId) == null) {
            System.out.println("Find ID " + missingId + ": no review found.");
            System.out.println("Update ID " + missingId + ": skipped, nothing to update.");
            System.out.println("Delete ID " + missingId + ": skipped, nothing to delete.");
        }

        // Bad input should not have changed anything that was stored
        System.out.println();
        System.out.println("===== FINAL LIST (unchanged by the bad input) =====");
        printAll(repository);
    }

    // Prints every review, or a message if there are none
    private static void printAll(MediaReviewRepository repository) {
        ArrayList<MediaReview> reviews = repository.findAll();
        if (reviews.isEmpty()) {
            System.out.println("(no reviews)");
            return;
        }
        for (MediaReview review : reviews) {
            System.out.println(review);
        }
        System.out.println("Total: " + reviews.size() + " review(s)");
    }
}
