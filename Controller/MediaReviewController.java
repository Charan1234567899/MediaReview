package MediaReview.Controller;

import MediaReview.Model.MediaReview;
import MediaReview.Model.MediaReviewRepository;
import MediaReview.View.MediaReviewView;
import java.util.ArrayList;

/**
 * The "middleman" between the window (View) and the data (Model).
 * When a button is clicked, the View calls a method here. The Controller
 * reads what the user typed, checks it, tells the repository what to do,
 * and then tells the View to refresh.
 */
public class MediaReviewController {

    private MediaReviewRepository repository; // interface type -> polymorphism
    private MediaReviewView view;
    private int selectedId; // ID of the review loaded in the form (0 = none)

    public MediaReviewController(MediaReviewRepository repository, MediaReviewView view) {
        this.repository = repository;
        this.view = view;
        this.selectedId = 0;
    }

    // ---------- Create New ----------
    public void createReview() {
        String title = view.getTitleInput();
        if (title.isEmpty()) {
            view.showError("Please enter a Media Title.");
            return;
        }

        try {
            // ID 0 tells the repository this is a brand-new record
            MediaReview review = new MediaReview(0, title, view.getScoreInput(),
                    view.getDirectorInput(), view.getGenreInput(),
                    view.getNotesInput(), view.getFavoriteInput());

            MediaReview saved = repository.save(review); // assigns the real ID and writes the file
            view.showMessage("Saved \"" + title + "\" as review #" + saved.getId() + ".");
            clearForm();
            refreshTable();
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    // ---------- Update Entry ----------
    public void updateReview() {
        // Step 1: if no review is loaded, ask for an ID and load it into the boxes
        if (selectedId == 0) {
            int id = askForId("Enter the ID of the review to update:");
            if (id == -1) {
                return;
            }
            MediaReview found = repository.findById(id);
            if (found == null) {
                view.showError("No review found with ID " + id + ".");
                return;
            }
            reviewSelected(found);
            view.showMessage("Review #" + id + " is loaded.\n"
                    + "Make your changes, then click Update Entry again.");
            return;
        }

        // Step 2: a review is loaded, so save the changes to it
        MediaReview review = repository.findById(selectedId);
        if (review == null) {
            view.showError("That review no longer exists.");
            clearForm();
            return;
        }

        String title = view.getTitleInput();
        if (title.isEmpty()) {
            view.showError("Please enter a Media Title.");
            return;
        }

        try {
            review.setTitle(title);
            review.setScore(view.getScoreInput());
            review.setDirector(view.getDirectorInput());
            review.setGenre(view.getGenreInput());
            review.setFavorite(view.getFavoriteInput());

            // setNotes() adds to the notes with a timestamp, so only call it if something was typed
            String newNotes = view.getNotesInput();
            if (!newNotes.isEmpty()) {
                review.setNotes(newNotes);
            }

            repository.save(review);
            view.showMessage("Review #" + review.getId() + " updated.");
            clearForm();
            refreshTable();
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    // ---------- Delete ----------
    public void deleteReview() {
        int id = selectedId;
        if (id == 0) {
            id = askForId("Enter the ID of the review to delete:");
            if (id == -1) {
                return;
            }
        }

        MediaReview review = repository.findById(id);
        if (review == null) {
            view.showError("No review found with ID " + id + ".");
            return;
        }

        if (view.confirm("Delete review #" + id + " (" + review.getTitle() + ")?")) {
            repository.deleteById(id);
            clearForm();
            refreshTable();
            view.showMessage("Review #" + id + " deleted.");
        }
    }

    // ---------- View Table ----------
    public void showTable() {
        view.showTable(repository.findAll());
    }

    // ---------- Clear ----------
    public void clearForm() {
        selectedId = 0;
        view.clearForm();
    }

    // Called when the user clicks a row in the table (or loads a review by ID)
    public void reviewSelected(MediaReview review) {
        selectedId = review.getId();
        view.fillForm(review);
    }

    // ---------- Helpers ----------
    private void refreshTable() {
        view.refreshTable(repository.findAll());
    }

    // Asks for an ID and validates it. Returns -1 if cancelled or invalid.
    private int askForId(String message) {
        String input = view.askForInput(message);
        if (input == null) {
            return -1; // Cancel was clicked
        }
        try {
            int id = Integer.parseInt(input.trim());
            if (id <= 0) {
                view.showError("The ID must be a positive whole number.");
                return -1;
            }
            return id;
        } catch (NumberFormatException e) {
            view.showError("\"" + input + "\" is not a valid ID number.");
            return -1;
        }
    }
}
