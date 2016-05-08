package me.oss.tracker.trackme;

/**
 * Created by Abdullah on 9/10/2015.
 */
public class LocalLog {
    public static String Employee_off_pos_json="";
    //For the simple app live server
    //public static String Emp_pos_link = "http://119.148.13.3/vehicle/api/employeeposition/getposition?";
    //public static String Emp_pos_link="http://119.148.13.5/sales/api/employeeposition/getposition?";
    //public static String Emp_off_link="http://119.148.13.5/sales/api/employeeposition/receiveofflinedatas";

    //Local PC
    public static String Emp_pos_link="http://119.148.13.3/Sales/api/employeeposition/getposition?";
//    public static String Emp_off_link="http://192.168.1.19/Sales/employeeposition/receiveofflinedatas";
    //http://192.168.1.19/Sales
   //for the school bus app
   //public static String Emp_pos_link="http://192.168.1.16/vehicleTracking/api/TrackingApp/getposition?";


    public static double latitude;
    public static double longitude;
    public static String _proviver;
    public static int wifi_state = 0;
    public static int syn_time = 6000;

    public static String message="";
    public static String title="";

}
