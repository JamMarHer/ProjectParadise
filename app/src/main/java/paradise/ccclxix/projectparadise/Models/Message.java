package paradise.ccclxix.projectparadise.Models;

import java.util.Date;

public class Message {
    /*
    Message Schema
    -------------------------
    id              :String
    from            :User
    to              :User
    body            :String
    sent            :Date
    seen            :Boolean
    attachment      :String
    */

    private String id;
    private User from;
    private User to;
    private String body;
    private Date sent;
    private Boolean seen;
    private String attachment = null;
}
