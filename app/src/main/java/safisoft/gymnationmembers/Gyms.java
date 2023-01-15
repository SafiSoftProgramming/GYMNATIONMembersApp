package safisoft.gymnationmembers;

public class Gyms {
    private int id;
    private String gym_name;
    private String gym_logo;
    private String gym_database_url;
    private String gym_address;


    public Gyms(int id, String gym_name, String gym_logo, String gym_database_url , String gym_address) {
        this.id = id;
        this.gym_name = gym_name;
        this.gym_logo = gym_logo;
        this.gym_database_url = gym_database_url;
        this.gym_address = gym_address ;


    }

    public int getId() {
        return id;
    }

    public String getGym_nameg() {
        return gym_name;
    }

    public String getGym_logo() {
        return gym_logo;
    }

    public String getGym_database_url() {
        return gym_database_url;
    }

    public String getGym_address() {
        return gym_address;
    }

}




