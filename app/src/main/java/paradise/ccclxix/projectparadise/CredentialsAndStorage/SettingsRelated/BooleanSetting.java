package paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;

public class BooleanSetting implements Setting {

    private String name;
    private String description;
    private boolean value;

    public BooleanSetting(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public BooleanSetting(String name, String description, boolean value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String getType() {

        return "BOOL";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public boolean getValue(){
        return this.value;
    }
}

