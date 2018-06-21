package paradise.ccclxix.projectparadise.utils;

public class TwitterHelpers {

    public static String getTweetID(String link){
        if (link.contains("status/")){
            String[] linkA = link.split("status/");
            if(linkA[linkA.length-1].contains("?")){
                String [] linkB = linkA[linkA.length-1].split("/?");
                return linkB[0];
            }else {
                return linkA[linkA.length-1];
            }
        }
        return "";
    }
}
