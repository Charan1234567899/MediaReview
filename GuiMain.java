package MediaReview;

import MediaReview.controller.MediaReviewController;
import MediaReview.model.InMemoryMediaReviewRepository;
import MediaReview.model.MediaReviewRepository;
import MediaReview.model.PersistantMediaReviewRepository;
import MediaReview.model.RepositoryException;
import MediaReview.view.MediaReviewView;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Starts the Media Reviewer Log.
 * Main is the only place that picks which repository is used.
 */
public class Main {

    public static void main(String[] args) {
        // All Swing work happens on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        });
    }

    private static void startApp() {
        useNimbusLookAndFeel();

        MediaReviewRepository repository;
        try {
            // ===== To switch storage, change ONLY the next line =====
            repository = new PersistantMediaReviewRepository("MediaReviews.txt");
            // repository = new InMemoryMediaReviewRepository();
        } catch (RepositoryException e) {
            // The save file couldn't be loaded: say so in plain words and stop,
            // instead of opening the app as if the data had loaded.
            JOptionPane.showMessageDialog(null,
                    e.getMessage() + "\nThe Media Reviewer Log will close so your saved reviews are not lost.",
                    "Could not load reviews", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MediaReviewView view = new MediaReviewView();
        MediaReviewController controller = new MediaReviewController(repository, view);
        view.setController(controller);
        controller.start();
        view.setVisible(true);
    }

    // Uses the Nimbus look if it is installed; otherwise Java's default look is kept
    private static void useNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Not a problem: the app still works with the default look
            UIManager.getLookAndFeelDefaults();
        }
    }
}
