package safisoft.gymnationmembers;

/**
 * Created by Belal on 10/18/2017.
 */

public class Member {
    private int id;
    private String members_name;
    private String workout_one_name;
    private String workout_two_name;
    private String members_photo;
    private String enter_member_time_date;

    public Member(int id, String members_name, String workout_one_name, String workout_two_name, String members_photo, String enter_member_time_date) {
        this.id = id;
        this.members_name = members_name;

        this.workout_one_name = workout_one_name;
        this.workout_two_name = workout_two_name;
        this.members_photo = members_photo;
        this.enter_member_time_date = enter_member_time_date ;
    }

    public int getId() {
        return id;
    }

    public String getMembersNmae() { return members_name; }

    public String getWorkOutOneName() {
        return workout_one_name;
    }

    public String getWorkOutTwoName() {
        return workout_two_name;
    }

    public String getMembersPhoto() { return members_photo; }

    public String getEnterMemberTimeDate() { return enter_member_time_date; }
}
