package paradise.ccclxix.projectparadise.Models;

import android.location.Geocoder;

import java.util.ArrayList;
import java.util.Date;

public class Wave {
    /*
    Wave Schema
    -------------------------
    id              :String
    name            :String
    creation        :Date
    members         [List of :User]
    living_posts    [List of :Post]
    dead_posts      [List of :Post]
    logo            :String
    privacy         :Enum
    Location        :Geo
    */

    enum Privacy{
        PUBLIC_EVERYWHERE, PRIVATE_EVERYWHERE, PUBLIC_LOCATION, PRIVATE_LOCATION
    }

    private String id;
    private String name;
    private Date creation;
    private ArrayList<User> members;
    private ArrayList<Post> living_posts;
    private ArrayList<Post> dead_posts;
    private String logo;
    private Privacy privacy;
    private Geocoder location;

}
