package paradise.ccclxix.projectparadise.Models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Post {
    /*
    User Schema
    -------------------------
    id              :String
    author          :String
    caption         :String
    attachment      :String
    comments        [List of :Comment]
    last-echo       :User
    date-created    :Date
    wave            [List of :Wave]
    death           :Date
    echoed-by       [List of :User]
    */

    private String id;
    private String author;
    private String caption;
    private String attachments;
    private List<User> echoed_by;

    public Post(String title, String u){

    }

    public HashMap<String, String> render(){
        HashMap<String, String> map = new HashMap<String, String>();
        //default
        return map;
    }
}
