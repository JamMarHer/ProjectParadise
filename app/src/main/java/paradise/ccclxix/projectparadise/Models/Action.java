package paradise.ccclxix.projectparadise.Models;

import java.util.Date;

public class Action {
    /*
    Action Schema
    -------------------------
    id              :String
    type            :Enum
    date            :Date
    user            :User
    message         :Message
    post            :Post
    comment         :Comment
    wave            :Wave
    */

    enum Type{
        MESSAGE, POST, COMMENT, WAVE;
    }

    private String id;
    private Type type;
    private Date date;
    private User user;
    private Message message = null;
    private Post post = null;
    private Comment comment = null;
    private Wave wave = null;
}
