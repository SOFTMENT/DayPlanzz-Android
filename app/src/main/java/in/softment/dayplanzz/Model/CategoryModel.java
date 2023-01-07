package in.softment.dayplanzz.Model;

public class CategoryModel {

    public String id = "";
    public String name = "";
    public boolean hasSubcategory = false;
    public boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasSubcategory() {
        return hasSubcategory;
    }

    public void setHasSubcategory(boolean hasSubcategory) {
        this.hasSubcategory = hasSubcategory;
    }
}
