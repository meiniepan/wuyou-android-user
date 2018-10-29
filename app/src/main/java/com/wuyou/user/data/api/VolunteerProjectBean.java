package com.wuyou.user.data.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by DELL on 2018/10/26.
 */

public class VolunteerProjectBean implements Parcelable{


    /**
     * id : 0
     * creator : samkunnbanb1
     * name : task8
     * service_time : 2019-12-21T10:00:00.000
     * address : address xxx
     * enroll_time : 2019-11-23T10:00:00.000
     * detailfile : file ipfs
     * positions : [{"name":"position_name_1","amount":8,"rewards":"10.0000 EOS","enrolled":[{"volunteer":"samkunnbanb2","registered":0,"rewards":0},{"volunteer":"samkunnbanb2","registered":1,"rewards":0}]}]
     */

    public int id;
    public String creator;
    public String name;
    public String service_time;
    public String service_end_time;
    public String address;
    public String enroll_time;
    public String enroll_end_time;
    public String detailfile;
    public String logofile;
    public List<PositionsBean> positions;

    public static class PositionsBean implements Parcelable{
        /**
         * name : position_name_1
         * amount : 8
         * rewards : 10.0000 EOS
         * enrolled : [{"volunteer":"samkunnbanb2","registered":0,"rewards":0},{"volunteer":"samkunnbanb2","registered":1,"rewards":0}]
         */

        public String name;
        public int amount;
        public String rewards;
        public List<EnrolledBean> enrolled;

        public static class EnrolledBean implements Parcelable{
            /**
             * volunteer : samkunnbanb2
             * registered : 0
             * rewards : 0
             */

            public String volunteer;
            public int registered;
            public int rewards;

            protected EnrolledBean(Parcel in) {
                volunteer = in.readString();
                registered = in.readInt();
                rewards = in.readInt();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(volunteer);
                dest.writeInt(registered);
                dest.writeInt(rewards);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<EnrolledBean> CREATOR = new Creator<EnrolledBean>() {
                @Override
                public EnrolledBean createFromParcel(Parcel in) {
                    return new EnrolledBean(in);
                }

                @Override
                public EnrolledBean[] newArray(int size) {
                    return new EnrolledBean[size];
                }
            };
        }

        protected PositionsBean(Parcel in) {
            name = in.readString();
            amount = in.readInt();
            rewards = in.readString();
            enrolled = in.createTypedArrayList(EnrolledBean.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeInt(amount);
            dest.writeString(rewards);
            dest.writeTypedList(enrolled);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<PositionsBean> CREATOR = new Creator<PositionsBean>() {
            @Override
            public PositionsBean createFromParcel(Parcel in) {
                return new PositionsBean(in);
            }

            @Override
            public PositionsBean[] newArray(int size) {
                return new PositionsBean[size];
            }
        };
    }


    protected VolunteerProjectBean(Parcel in) {
        id = in.readInt();
        creator = in.readString();
        name = in.readString();
        service_time = in.readString();
        service_end_time = in.readString();
        address = in.readString();
        enroll_time = in.readString();
        enroll_end_time = in.readString();
        detailfile = in.readString();
        logofile = in.readString();
        positions = in.createTypedArrayList(PositionsBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(creator);
        dest.writeString(name);
        dest.writeString(service_time);
        dest.writeString(service_end_time);
        dest.writeString(address);
        dest.writeString(enroll_time);
        dest.writeString(enroll_end_time);
        dest.writeString(detailfile);
        dest.writeString(logofile);
        dest.writeTypedList(positions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VolunteerProjectBean> CREATOR = new Creator<VolunteerProjectBean>() {
        @Override
        public VolunteerProjectBean createFromParcel(Parcel in) {
            return new VolunteerProjectBean(in);
        }

        @Override
        public VolunteerProjectBean[] newArray(int size) {
            return new VolunteerProjectBean[size];
        }
    };
}