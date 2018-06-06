package paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;

public class StringSetting implements Setting {

    private String name;
    private String description;
    private String value;

    public StringSetting(String name, String description){
        this.name = name;
        this.description = description;
    }

    public StringSetting(String name, String description, String value){
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return "STR";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
