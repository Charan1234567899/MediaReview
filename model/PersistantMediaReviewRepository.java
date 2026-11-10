package MediaReview.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Works exactly like InMemoryMediaReviewRepository (it inherits findAll and findById),
 * but every save or delete is also written to a text file, so reviews are still
 * there the next time the app is opened.
 *
 * If the file can't be read or written, a RepositoryException is thrown
 * so the Controller (or Main) can tell the user what went wrong.
 *
 * File format - one review per line, fields separated by "|":
 *   id|title|score|director|genre|notes|favorite
 */
public class PersistantMediaReviewRepository extends InMemoryMediaReviewRepository {

    private static final String DELIMITER = "|";
    private String filePath;

    public PersistantMediaReviewRepository(String filePath) {
        super(); // sets up the empty ArrayList and nextId = 1
        this.filePath = filePath;
        loadFromFile();
    }

    public PersistantMediaReviewRepository() {
        this("MediaReviews.txt");
    }

    @Override
    public MediaReview save(MediaReview review) {
        MediaReview saved = super.save(review); // update the list (assigns an ID if it is new)
        saveToFile();                           // then write the whole list to the file
        return saved;
    }

    @Override
    public void deleteById(int id) {
        super.deleteById(id);
        saveToFile();
    }

    // Reads every line of the file and rebuilds the MediaReview objects.
    // Missing file -> first run, so create an empty one.
    // Damaged line or unreadable file -> throw RepositoryException so the user is told.
    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            saveToFile(); // first run - create the empty file and carry on with an empty list
            return;
        }

        Scanner input = null;
        try {
            input = new Scanner(file);
            int lineNumber = 0;
            while (input.hasNextLine()) {
                String line = input.nextLine();
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue; // blank lines are fine, just skip them
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length != 7) {
                    throw new RepositoryException("Line " + lineNumber + " of " + filePath
                            + " is damaged (wrong number of fields).");
                }

                try {
                    int id = Integer.parseInt(parts[0]);
                    double score = Double.parseDouble(parts[2]);
                    boolean favorite = Boolean.parseBoolean(parts[6]);

                    MediaReview review = new MediaReview(id, decode(parts[1]), score,
                            decode(parts[3]), decode(parts[4]), decode(parts[5]), favorite);

                    // Add it using the parent's save() (it already has an ID, so it just gets added).
                    // super.save() is used so the file is not rewritten while we are still reading it.
                    super.save(review);
                } catch (IllegalArgumentException e) {
                    // covers NumberFormatException and an out-of-range score
                    throw new RepositoryException("Line " + lineNumber + " of " + filePath
                            + " is damaged (" + e.getMessage() + ").");
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Could not open the save file " + filePath + ".");
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    // Rewrites the whole file from the current list
    private void saveToFile() {
        PrintWriter output = null;
        try {
            output = new PrintWriter(new FileWriter(filePath));
            for (MediaReview review : findAll()) {
                output.println(review.getId() + DELIMITER
                        + encode(review.getTitle()) + DELIMITER
                        + review.getScore() + DELIMITER
                        + encode(review.getDirector()) + DELIMITER
                        + encode(review.getGenre()) + DELIMITER
                        + encode(review.getNotes()) + DELIMITER
                        + review.isFavorite());
            }
        } catch (IOException e) {
            throw new RepositoryException("Could not save your reviews to " + filePath + ".");
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    // Each review must stay on one line and "|" splits the fields,
    // so swap those characters out before writing...
    private String encode(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("|", "/").replace("\n", "<br>");
    }

    // ...and put the line breaks back when reading
    private String decode(String text) {
        return text.replace("<br>", "\n");
    }
}
