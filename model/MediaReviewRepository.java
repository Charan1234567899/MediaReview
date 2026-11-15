package MediaReview.model;
import java.util.ArrayList;
/*
* Unit 13 - Abstract Classes and Interfaces:
* The interface is created to ensure both InMemory and Persistent MediaReview repositories have the methods 
* findAll(), FindbyID(), save(), and deleteByID(). This ensures that both classes have consistency, especially in
* the context of their later calls in the GUI interface.
*/
public interface MediaReviewRepository {

    public ArrayList<MediaReview> findAll();

    public MediaReview findById(int id);

    public MediaReview save(MediaReview review); // insert if id == 0, else update

    public void deleteById(int id);

}
