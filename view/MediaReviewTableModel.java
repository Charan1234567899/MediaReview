package MediaReview.View;

import MediaReview.Model.MediaReview;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * Tells the JTable how to show the list of reviews:
 * how many rows/columns there are and what goes in each cell.
 */
public class MediaReviewTableModel extends AbstractTableModel {

    private String[] columnNames = {"ID", "Title", "Score", "Creator", "Genre", "Favorite", "Notes"};
    private ArrayList<MediaReview> reviews;

    public MediaReviewTableModel(ArrayList<MediaReview> reviews) {
        this.reviews = reviews;
    }

    // Swap in a fresh list and tell the table to redraw itself
    public void setReviews(ArrayList<MediaReview> newReviews) {
        reviews = newReviews;
        fireTableDataChanged();
    }

    public MediaReview getReviewAt(int row) {
        return reviews.get(row);
    }

    @Override
    public int getRowCount() {
        return reviews.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 0) {
            return Integer.class;
        } else if (column == 2) {
            return Double.class;
        } else if (column == 5) {
            return Boolean.class; // shows a checkbox
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        MediaReview review = reviews.get(row);
        switch (column) {
            case 0: return review.getId();
            case 1: return review.getTitle();
            case 2: return review.getScore();
            case 3: return review.getDirector();
            case 4: return review.getGenre();
            case 5: return review.isFavorite();
            case 6: return review.getNotes().replace("\n", "  ");
            default: return "";
        }
    }
}
