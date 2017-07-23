package uk.co.zac_h.message.database.databaseModel;

public class ProfileModel {

    private String name;
    private Integer firstRun;

    public ProfileModel() {}

    public ProfileModel(String name, Integer firstRun) {
        this.name = name != null? name : this.name;
        this.firstRun = firstRun != null? firstRun : this.firstRun;
    }

    public String getName() {
        return name;
    }

    public Integer getFirstRun() {
        return firstRun;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstRun(Integer firstRun) {
        this.firstRun = firstRun;
    }
}
