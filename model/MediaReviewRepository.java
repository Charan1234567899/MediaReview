package MediaReview.model;
import java.util.ArrayList;

public interface MediaReviewRepository {

    public ArrayList<MediaReview> findAll();

    public MediaReview findById(int id);

    public MediaReview save(MediaReview review); // insert if id == 0, else update

    public void deleteById(int id);

}
