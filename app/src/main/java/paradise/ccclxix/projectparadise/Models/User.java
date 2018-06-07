package paradise.ccclxix.projectparadise.Models;

import java.util.Date;
import java.util.HashMap;

public class User {
    /*
    User Schema
    -------------------------
    name            :String
    email           :String
    phone           :String
    username        :String
    password        :String
    status          :String
    thumb_image     :String
    join            :Date
    waves           [List of :Wave]
    history         [List of :Action]
    */

    private String name;
    private String email;
    private String phone;
    private String username;
    private String password;
    private String status;
    private String thumb_image;
    private Date join;

    public User(String u){
        username = u;
    }

    public HashMap<String, String> render(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        //default
        map.put("status", "We lit");
        map.put("thumb_image", "default");
        return map;
    }
}
