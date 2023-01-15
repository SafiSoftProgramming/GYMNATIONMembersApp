package safisoft.gymnationmembers;

public class Workout {

    private int id;
    private String member_name;
    private String member_photo;
    private String workout_one_name;
    private String workout_two_name;
    private String start_workout_date;
    private String start_workout_time;
    private String end_workout_time;
    private String workout_time_duration;
    private String workout_rate;
    private String workout_sign_out_mode;
    private String rfid;


    public Workout(int id, String member_name, String member_photo, String workout_one_name, String workout_two_name,String start_workout_date , String start_workout_time, String end_workout_time, String workout_time_duration, String workout_rate,String workout_sign_out_mode,String rfid) {
        this.id = id;
        this.member_name = member_name;
        this.member_photo = member_photo;
        this.workout_one_name = workout_one_name;
        this.workout_two_name = workout_two_name;
        this.start_workout_date = start_workout_date ;
        this.start_workout_time = start_workout_time;
        this.end_workout_time = end_workout_time;
        this.workout_time_duration = workout_time_duration;
        this.workout_rate = workout_rate;
        this.workout_sign_out_mode = workout_sign_out_mode;
        this.rfid = rfid ;
    }

    public int getId() {
        return id;
    }

    public String getMemberName() { return member_name; }

    public String getMemberPhoto() {
        return member_photo;
    }

    public String getWorkoutOneName() {
        return workout_one_name;
    }

    public String getWorkoutTwoName() {
        return workout_two_name;
    }

    public String getstartWorkoutDate() { return start_workout_date; }

    public String getstartWorkoutTime() { return start_workout_time; }

    public String getendWorkoutTime() {
        return end_workout_time;
    }

    public String getWorkoutTimeDuration() { return workout_time_duration; }

    public String getWorkoutRate() {
        return workout_rate;
    }

    public String getWorkoutSignOutMode() {
        return workout_sign_out_mode;
    }

    public String getRfid() { return rfid; }





}
