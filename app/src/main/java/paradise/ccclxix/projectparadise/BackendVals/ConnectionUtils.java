package paradise.ccclxix.projectparadise.BackendVals;

/**
 * Created by jam on 1/26/18.
 */

public class ConnectionUtils {
    // LocalHost under simulation is 10.0.2.2
    public static final String MAIN_SERVER           = "http://10.0.2.2:5000";
    public static final String REGISTER              = MAIN_SERVER + "/submit_create_account";
    public static final String LOG_IN                = MAIN_SERVER + "/submit_login";
    public static final String REQUEST_USER_TABLE    = MAIN_SERVER + "/request_user_table";
    public static final String CHECK_USER_STATUS     = MAIN_SERVER + "/check_user_status";
    public static final String UN_AUTH_TOKEN         = MAIN_SERVER + "/unauth_token";
}
