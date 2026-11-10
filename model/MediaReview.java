package MediaReview.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaReview {

    //Creation of variables for Media Review Class
    private int id;
    private String title;
    private double score;
    private String director;
    private String genre;
    private String notes;
    private boolean favorite;


    //Main Media Review Constructor
    public MediaReview(int id, String title, double score, String director, String genre, String notes, boolean favorite) {
        this.id = id;
        this.title = title;
        setScore(score); // uses the setter so the score is checked
        this.director = director;
        this.genre = genre;
        this.notes = notes;
        this.favorite = favorite;
    }

    //Creation of all main getters

    public int getId() {
        return id;
    }

    public double getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre(){
        return genre;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isFavorite() {
        return favorite;
    }

    //Creation of all main setters

    public void setScore(double newScore) {
        if (newScore < 0 || newScore > 5) {
            throw new IllegalArgumentException("Score must be between 0 and 5.");
        }
        score = newScore;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public void setDirector(String newDirector) {
        director = newDirector;
    }

    public void setGenre(String newGenre) {
        genre = newGenre;
    }

    public void setNotes(String newNotes) {
        notes += ("\nUpdated Notes @ " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) + "\n" + newNotes);
    }

    public void setFavorite(boolean newFav){
        favorite = newFav;
    }

    // A new review starts with ID 0 (meaning "no ID yet").
    // When it is saved, the repository class (InMemoryMediaReviewRepository)
    // calls this method to give it its real ID number: 1, 2, 3, and so on.
    public void setId(int newId) {
        id = newId;
    }

    @Override
    public String toString() {
        return "#" + id + " " + title + " (" + score + "/5) by " + director
                + " | " + genre + " | Favorite: " + favorite + "\nNotes: " + notes;
    }
}
