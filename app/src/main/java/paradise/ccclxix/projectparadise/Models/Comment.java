package paradise.ccclxix.projectparadise.Models;

import java.util.ArrayList;
import java.util.Date;

public class Comment {
    /*
    Comment Schema
    -------------------------
    id              :String
    author          :User
    created         :Date
    body            :String
    reply_to        :Comment
    replies         [List of :Comment]
    */

    private String id;
    private User author;
    private Date created;
    private String body;
    private Comment reply_to = null;
    private ArrayList<String> replies;
}
