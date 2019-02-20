package com.app.shovelerapp.netutils;

/**
 * Created by supriya.n on 05-07-2016.
 */
public class Webservices {
    //public static String base_url="http://192.168.1.122:88/shovler/api/";
    public static String base_url="http://carshovel.com/test/api/";

    //public static String base_url="http://carshovel.com/api/";
    public static String Get_Job_Type_Prices=base_url+"jobmgt.php/statepricemaster/";
    public static String Reg_Job_Home=base_url+"jobmgt.php/jobpost/";
    public static String Reg_Job_Car=base_url+"jobmgt.php/jobpost/";
    public static String Reg_Job_Business=base_url+"jobmgt.php/jobpost/";
    public static String Calculate_business_price=base_url+"jobmgt.php/getbusinesscost/?";
    public static String Job_Profile_Requestor=base_url+"jobmgt.php/rjobprofile/";
    public static String Job_Profile_shoveler=base_url+"jobmgt.php/sjobprofile/";
    public static String get_available_jobs=base_url+"jobmgt.php/alljobs/";
    //public static String accept_job=base_url+"jobmgt.php/acceptjob/";
    public static String accept_job=base_url+"jobmgt.php/acceptjobtest/";
    public static String cancel_job=base_url+"jobmgt.php/cancelsjob/";
    public static String finish_job=base_url+"jobmgt.php/finishsjobtest/?";
    //public static String finish_job=base_url+"jobmgt.php/finishsjob/?";

    // static data APIS
    public static String terms=base_url+"jobmgt.php/cms/";
    public static String faq=base_url+"jobmgt.php/faq";
    public static String about=base_url+"jobmgt.php/cms/";
    public static String feedback=base_url+"jobmgt.php/feedback/";
    public static String relunch=base_url+"jobmgt.php/rcancelopenjob/";
    public static String cancels_job=base_url+"jobmgt.php/rcanceljob/";
    public static String hold_cancels_job=base_url+"jobmgt.php/rholdjobscaneloropen/";
    public static String privacy=base_url+"jobmgt.php/cms/";
    public static String add_promocode=base_url+"userreg.php/addprmocode/";
    public static String jobs= base_url + "jobmgt.php/alljobs/";
    public static String approve_job=base_url+"jobmgt.php/donerjob/";
    public static String update_latlng=base_url+"userreg.php/shovlercurloc/";
    public static String get_promocode  =base_url+"jobmgt.php/getallpcode";
    public static String logout  =base_url+"jobmgt.php/logout/";

    public static String check_promocode=base_url+"jobmgt.php/validpromocode/";


}
