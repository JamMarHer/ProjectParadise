package paradise.ccclxix.projectparadise.utils;

import paradise.ccclxix.projectparadise.R;

public class Icons {

    public static final int FIRE = R.drawable.fire_emoji;
    public static final int POOP = R.drawable.poop_icon;
    public static final int COOL = R.drawable.cool;
    public static final int NON = -1;

    private static final int[] icons = new int[]{FIRE,POOP,COOL};

    public static boolean isIcon(int ic){
        for (int i = 0; i < icons.length; i++){
            if (icons[i] == ic)
                return true;
        }
        return false;
    }
}
