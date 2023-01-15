package safisoft.gymnationmembers;



public class AdsPost {
    private int id;
    private String ad_desc;
    private String ad_img;
    private String ad_name;
    private String ad_icon;
    private String ad_time_date;
    private String promo_code;
    private String promo_code_expiry_date;
    private String contact_details;





    public AdsPost(int id, String ad_desc, String ad_img, String ad_name, String ad_icon, String ad_time_date, String promo_code, String promo_code_expiry_date, String contact_details) {
        this.id = id;
        this.ad_desc = ad_desc;
        this.ad_img = ad_img;
        this.ad_name = ad_name;
        this.ad_icon = ad_icon;
        this.ad_time_date = ad_time_date;
        this.promo_code = promo_code;
        this.promo_code_expiry_date = promo_code_expiry_date;
        this.contact_details = contact_details;

    }

    public int getId() {
        return id;
    }

    public String getAd_desc() {
        return ad_desc;
    }

    public String getAd_img() {
        return ad_img;
    }

    public String getad_name() {
        return ad_name;
    }

    public String getad_icon() {
        return ad_icon;
    }

    public String getad_time_date() {
        return ad_time_date;
    }

    public String getpromo_code() {
        return promo_code;
    }

    public String getpromo_code_expiry_date() {
        return promo_code_expiry_date;
    }

    public String getcontact_details() {
        return contact_details;
    }






}
