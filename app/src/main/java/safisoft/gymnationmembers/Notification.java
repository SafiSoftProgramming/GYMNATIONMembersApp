package safisoft.gymnationmembers;

/**
 * Created by Belal on 10/18/2017.
 */

public class Notification {
    private int id;
    private String massage_head;
    private String massage_body;
    private String massage_time_date;
    private String massage_icon;


    public Notification(int id, String massage_head, String massage_body, String massage_time_date, String massage_icon) {
        this.id = id;
        this.massage_head = massage_head;
        this.massage_body = massage_body;
        this.massage_time_date = massage_time_date;
        this.massage_icon = massage_icon;

    }

    public int getId() {
        return id;
    }

    public String getMassageHead() {
        return massage_head;
    }

    public String getMassageBody() {
        return massage_body;
    }

    public String getMassageTimeDate() {
        return massage_time_date;
    }

    public String getMassageIcon() {
        return massage_icon;
    }


}
