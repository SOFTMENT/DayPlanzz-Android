package in.softment.dayplanzz.Model;

import java.util.ArrayList;

public class FavoriteModel {

    public String uid = "";
    public String organiserName = "";

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrganiserName() {
        return organiserName;
    }

    public void setOrganiserName(String organiserName) {
        this.organiserName = organiserName;
    }

    public static ArrayList<FavoriteModel> favoriteModels  = new ArrayList<>();

}
