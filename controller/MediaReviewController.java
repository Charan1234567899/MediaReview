package MediaReview.controller;

import MediaReview.model.MediaReview;
import MediaReview.model.MediaReviewRepository;
import MediaReview.model.RepositoryException;
import MediaReview.view.MediaReviewView;

/**
 * The "middleman" between the window (View) and the data (Model).
 * When a button is clicked, the View calls a method here. The Controller
 * reads what the user typed, checks it, tells the repository what to do,
 * and then tells the View to refresh.
 */
public class MediaReviewController {

    private MediaReviewRepository repository; // interface type -> polymorphism
    private MediaReviewView view;
    private int selectedId; // ID of the review picked in the table (0 = nothing selected)

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
            return; // the user's other input stays in the boxes
        }

        try {
            // ID 0 tells the repository this is a brand-new record; it picks the real ID
            MediaReview review = new MediaReview(0, title, view.getScoreInput(),
                    view.getDirectorInput(), view.getGenreInput(),
                    view.getNotesInput(), view.getFavoriteInput());

            MediaReview saved = repository.save(review);
            clearForm();
            refreshTable();
            view.setStatus("Saved \"" + title + "\" as review #" + saved.getId() + ".");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage()); // e.g. a score outside 0 - 5
        } catch (RepositoryException e) {
            view.showError(e.getMessage() + "\nYour review may not have been saved.");
        }
    }

    // ---------- Update Entry ----------
    public void updateReview() {
        if (selectedId == 0) {
            view.showError("Nothing selected. Click View Table, then click the review you want to update.");
            return;
        }

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
            int id = review.getId();
            clearForm();
            refreshTable();
            view.setStatus("Review #" + id + " updated.");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        } catch (RepositoryException e) {
            view.showError(e.getMessage() + "\nYour changes may not have been saved.");
        }
    }

    // ---------- Delete ----------
    public void deleteReview() {
        if (selectedId == 0) {
            view.showError("Nothing selected. Click View Table, then click the review you want to delete.");
            return;
        }

        MediaReview review = repository.findById(selectedId);
        if (review == null) {
            view.showError("That review no longer exists.");
            clearForm();
            return;
        }

        int id = selectedId;
        if (view.confirm("Delete review #" + id + " (" + review.getTitle() + ")?")) {
            try {
                repository.deleteById(id);
                clearForm();
                refreshTable();
                view.setStatus("Review #" + id + " deleted.");
            } catch (RepositoryException e) {
                view.showError(e.getMessage() + "\nThe delete may not have been saved.");
            }
        } else {
            view.setStatus("Delete cancelled.");
        }
    }

    // ---------- View Table ----------
    public void showTable() {
        view.showTable(repository.findAll());
    }

    // ---------- Clear ----------
    // Empties the form and deselects, without changing any stored data
    public void clearForm() {
        selectedId = 0;
        view.clearForm();
        view.setStatus("Form cleared.");
    }

    // Called when the user clicks a row in the table
    public void reviewSelected(MediaReview review) {
        selectedId = review.getId();
        view.fillForm(review);
        view.setStatus("Review #" + selectedId + " selected. Edit it and click Update Entry, or click Delete.");
    }

    // Called once by Main when the window first opens
    public void start() {
        refreshTable();
        int count = repository.findAll().size();
        view.setStatus("Ready. " + count + " review(s) loaded.");
    }

    // ---------- Helpers ----------
    private void refreshTable() {
        view.refreshTable(repository.findAll());
    }
}
