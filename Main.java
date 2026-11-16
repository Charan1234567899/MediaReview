package MediaReview;

import MediaReview.model.InMemoryMediaReviewRepository;
import MediaReview.model.MediaReview;
import MediaReview.model.MediaReviewRepository;
import java.util.ArrayList;

/**
 * For Milestone 2 purposes: the console only Main (no GUI).
 * 
 *
 * @author Charan Kristem/Ethan Porter
 */
public class Main {

    public static void main(String[] args) {

        // ---------- SETUP ----------
        heading("SETUP");
        // Declared as the interface type, holding the in-memory implementation (polymorphism).
       
        MediaReviewRepository repo = new InMemoryMediaReviewRepository();
        System.out.println("Repository created. Reviews stored: " + repo.findAll().size());

        // ---------- CREATE ----------
        heading("CREATE (every new review starts with ID 0; the repository assigns the real ID)");
        tryCreate(repo, "Dune", "4.5", "Denis Villeneuve", "Sci-fi", "Amazing visuals and sound.", true);
        tryCreate(repo, "Interstellar", "5", "Christopher Nolan", "Sci-fi", "The docking scene!", true);
        tryCreate(repo, "Superbad", "3.5", "Greg Mottola", "Comedy", "Funny with friends.", false);
        tryCreate(repo, "The Dark Knight", "5", "Christopher Nolan", "Action", "Best Joker ever.", true);
        tryCreate(repo, "Inside Out", "4", "Pete Docter", "Comedy", "Made me cry.", false);
        tryCreate(repo, "Get Out", "4.5", "Jordan Peele", "Horror", "Smart and scary.", false);
        tryCreate(repo, "Mad Max: Fury Road", "4", "George Miller", "Action", "Non-stop chase.", false);
        tryCreate(repo, "The Notebook", "3", "Nick Cassavetes", "Romance", "Sweet but long.", false);

        // ---------- READ ALL ----------
        heading("READ ALL (findAll)");
        printAll(repo);

        // ---------- READ ONE ----------
        heading("READ ONE (findById(4), an ID that exists)");
        System.out.println("Found: " + repo.findById(4));

        // ---------- UPDATE ----------
        heading("UPDATE (save a NEW object that has existing ID 2 and changed values)");
        int countBeforeUpdate = repo.findAll().size();
        System.out.println("Before: " + repo.findById(2));
        MediaReview changed = new MediaReview(2, "Interstellar", 4.0,
                "Christopher Nolan", "Sci-fi", "Rewatched it, still good but long.", false);
        repo.save(changed);
        System.out.println("After:  " + repo.findById(2));
        System.out.println("Count before update: " + countBeforeUpdate
                + ", after update: " + repo.findAll().size() + " (same, so no second copy was added)");

        // ---------- DELETE ----------
        heading("DELETE (deleteById(3))");
        repo.deleteById(3);
        System.out.println("findById(3) after delete returns: " + repo.findById(3));
        printAll(repo);

        // ---------- BAD INPUT ----------
        heading("BAD INPUT (nothing here should be stored)");
        int countBeforeBad = repo.findAll().size();
        System.out.println("Count before bad input: " + countBeforeBad);
        System.out.println();

        System.out.print("1. Empty score:        ");
        tryCreate(repo, "Blank Score Movie", "", "Someone", "Other", "", false);

        System.out.print("2. Score as words:     ");
        tryCreate(repo, "Wordy Movie", "four", "Someone", "Other", "", false);

        System.out.print("3. Negative score:     ");
        tryCreate(repo, "Negative Movie", "-2", "Someone", "Other", "", false);

        System.out.print("4. Score above 5:      ");
        tryCreate(repo, "Too High Movie", "7", "Someone", "Other", "", false);

        System.out.print("5. Empty title:        ");
        tryCreate(repo, "", "3", "Someone", "Other", "", false);

        int missingId = 99;
        System.out.print("6. findById(" + missingId + "):     ");
        MediaReview missing = repo.findById(missingId);
        if (missing == null) {
            System.out.println("returned null, no review has ID " + missingId + ".");
        } else {
            System.out.println("ERROR: found " + missing);
        }

        System.out.print("7. deleteById(" + missingId + "):   ");
        int countBeforeDelete = repo.findAll().size();
        repo.deleteById(missingId);
        System.out.println("nothing deleted (count " + countBeforeDelete
                + " -> " + repo.findAll().size() + ").");

        System.out.print("8. Update ID " + missingId + ":     ");
        try {
            repo.save(new MediaReview(missingId, "Ghost Movie", 3, "Nobody", "Other", "", false));
            System.out.println("ERROR: the update was accepted.");
        } catch (IllegalArgumentException e) {
            System.out.println("rejected - " + e.getMessage());
        }

        System.out.println();
        int countAfterBad = repo.findAll().size();
        System.out.println("Count before bad input: " + countBeforeBad + ", after: " + countAfterBad);
        if (countBeforeBad == countAfterBad) {
            System.out.println("Same count, so no bad data was stored.");
        } else {
            System.out.println("ERROR: the count changed, so a check is missing.");
        }

        // ---------- DEFENSIVE COPY ----------
        heading("DEFENSIVE COPY (clear the list findAll() returns)");
        ArrayList<MediaReview> copy = repo.findAll();
        copy.clear();
        System.out.println("Size of the cleared list: " + copy.size());
        System.out.println("Reviews still in the repository: " + repo.findAll().size()
                + " (unchanged, because findAll() returns a copy)");
    }

    // Does what the Controller will do in M3 where it takes the score as text, converts it
    // to a number, and lets the MediaReview setters reject bad values.
    private static void tryCreate(MediaReviewRepository repo, String title, String scoreText,
                                  String director, String genre, String notes, boolean favorite) {
        try {
            double score = Double.parseDouble(scoreText);
            MediaReview saved = repo.save(new MediaReview(0, title, score, director, genre, notes, favorite));
            System.out.println("Saved with new ID " + saved.getId() + ": " + saved.getTitle());
        } catch (NumberFormatException e) {
            System.out.println("rejected - score \"" + scoreText + "\" is not a number.");
        } catch (IllegalArgumentException e) {
            System.out.println("rejected - " + e.getMessage());
        }
    }

    // Prints every review on one line each, then the count
    private static void printAll(MediaReviewRepository repo) {
        ArrayList<MediaReview> reviews = repo.findAll();
        for (MediaReview review : reviews) {
            System.out.println("  #" + review.getId() + "  " + review.getTitle()
                    + "  (" + review.getScore() + "/5)  " + review.getDirector()
                    + "  | " + review.getGenre() + "  | Favorite: " + review.isFavorite());
        }
        System.out.println("Total: " + reviews.size() + " review(s)");
    }

    private static void heading(String title) {
        System.out.println();
        System.out.println("===== " + title + " =====");
    }
}
