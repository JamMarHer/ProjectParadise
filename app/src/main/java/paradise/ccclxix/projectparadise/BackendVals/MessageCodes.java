package paradise.ccclxix.projectparadise.BackendVals;

public class MessageCodes {

    // Intended code ranges; 101-200 Network
    //                       200-250 Registration
    //                       250-300 Login
    //                       300-*   DB Exceptions and more.
    public final static int OK = 100;
    // Registration related.
    public final static int USER_NOT_AVAILABLE = 200;
    public final static int USERNAME_DOES_NOT_EXIST = 201;
    public final static int ID_DOES_NOT_EXIST = 202;
    public final static int EMAIL_NOT_AVAILABLE = 202;

    // Login related
    public final static int INCORRECT_LOGIN = 250;
    public final static int IMPOSIBLE_TOKEN_UPDATE = 251;
    public final static int INCORRECT_TOKEN = 252;

    // General
    public final static int PDO_EXCEPTION = 300;
    public final static int INCORRECT_FORMAT = 301;
    public final static int FAILED_CONNECTION = 302;
    public final static int SERVER_NOT_ALIVE = 303;
    public final static int NO_INTERNET_CONNECTION = 304;


}
