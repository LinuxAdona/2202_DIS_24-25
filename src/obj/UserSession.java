package obj;

public class UserSession {
    private static User currentUser ;

    public static void setCurrentUser (User user) {
        currentUser  = user;
    }

    public static String getCurrentUserID() {
        return currentUser  != null ? currentUser.getUserID() : null;
    }
}