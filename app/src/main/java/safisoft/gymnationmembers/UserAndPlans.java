package safisoft.gymnationmembers;

public class UserAndPlans {

    private int id;
    private String plan_name;
    private String plan_description;
    private String plan_duration;
    private String plan_price;
    private String plan_available;



    public UserAndPlans(int id, String plan_name, String plan_description, String plan_duration, String plan_price, String plan_available ) {
        this.id = id;
        this.plan_name = plan_name;
        this.plan_description = plan_description;
        this.plan_duration = plan_duration;
        this.plan_price = plan_price;
        this.plan_available = plan_available ;

    }

    public int getId() {
        return id;
    }

    public String getPlanName() { return plan_name; }

    public String getPlanDescription() {
        return plan_description;
    }

    public String getPlanDuration() {
        return plan_duration;
    }

    public String getPlanPrice() { return plan_price; }

    public String getPlanAvailable() { return plan_available; }





}
