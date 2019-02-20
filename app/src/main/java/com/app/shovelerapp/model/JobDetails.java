package com.app.shovelerapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.io.File;
import java.io.Serializable;

/**
 * Created by supriya.n on 27-06-2016.
 */
public class JobDetails implements Serializable{
    /**
     * address : test address
     * lat : 0.0
     * lng : 0.0
     * description : ""
     * Car : {"model":"Honda city","color":"Red","state":"Maharastra","license":"MH12345","price":"80","url":"image.jpg"}
     * Home : {"price":"80","url":"image.jpg"}
     * Business : {"size":"45","price":"80","url":"image.jpg"}
     */

    private JobDetailsEntity JobDetails;

    public JobDetails(){}


    public static JobDetails objectFromData(String str) {

        return new Gson().fromJson(str, JobDetails.class);
    }

    public JobDetailsEntity getJobDetails() {
        return JobDetails;
    }

    @Override
    public String toString() {
        return "JobDetails{" +
                "JobDetails=" + JobDetails +
                '}';
    }

    public void setJobDetails(JobDetailsEntity JobDetails) {
        this.JobDetails = JobDetails;
    }


    public static class JobDetailsEntity {
        private String address;
        private String zipcode;
        private String lat;
        private String lng;
        public String exptime;

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

        private String desc;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        /**
         * model : Honda city
         * color : Red
         * state : Maharastra
         * license : MH12345
         * price : 80
         * url : image.jpg
         */

        private CarEntity Car;
        /**
         * price : 80
         * url : image.jpg
         */

        private HomeEntity Home;
        /**
         * size : 45
         * price : 80
         * url : image.jpg
         */

        private BusinessEntity Business;

        public static JobDetailsEntity objectFromData(String str) {

            return new Gson().fromJson(str, JobDetailsEntity.class);
        }

        @Override
        public String toString() {
            return "JobDetailsEntity{" +
                    "address='" + address + '\'' +
                    ", lat='" + lat + '\'' +
                    ", lng='" + lng + '\'' +
                    ", desc='" + desc + '\'' +
                    ", Car=" + Car +
                    ", Home=" + Home +
                    ", Business=" + Business +
                    '}';
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public CarEntity getCar() {
            return Car;
        }

        public void setCar(CarEntity Car) {
            this.Car = Car;
        }

        public HomeEntity getHome() {
            return Home;
        }

        public void setHome(HomeEntity Home) {
            this.Home = Home;
        }

        public BusinessEntity getBusiness() {
            return Business;
        }

        public void setBusiness(BusinessEntity Business) {
            this.Business = Business;
        }

        public static class CarEntity implements Serializable{
            private String model;
            private String color;
            private String state;
            private String license;
            private String price;
            private String url;
            private File file;

            public File getFile() {
                return file;
            }

            public void setFile(File file) {
                this.file = file;
            }

            public static CarEntity objectFromData(String str) {

                return new Gson().fromJson(str, CarEntity.class);
            }

            @Override
            public String toString() {
                return "CarEntity{" +
                        "model='" + model + '\'' +
                        ", color='" + color + '\'' +
                        ", state='" + state + '\'' +
                        ", license='" + license + '\'' +
                        ", price='" + price + '\'' +
                        ", url='" + url + '\'' +
                        '}';
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getLicense() {
                return license;
            }

            public void setLicense(String license) {
                this.license = license;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class HomeEntity implements Serializable{
            private String price;
            private String url;
            private File file;

            public File getFile() {
                return file;
            }

            public void setFile(File file) {
                this.file = file;
            }

            public static HomeEntity objectFromData(String str) {

                return new Gson().fromJson(str, HomeEntity.class);
            }

            @Override
            public String toString() {
                return "HomeEntity{" +
                        "price='" + price + '\'' +
                        ", url='" + url + '\'' +
                        '}';
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class BusinessEntity implements Serializable{
            private String size;
            private String price;
            private String url;
            private File file;

            public File getFile() {
                return file;
            }

            public void setFile(File file) {
                this.file = file;
            }

            public static BusinessEntity objectFromData(String str) {

                return new Gson().fromJson(str, BusinessEntity.class);
            }

            @Override
            public String toString() {
                return "BusinessEntity{" +
                        "size='" + size + '\'' +
                        ", price='" + price + '\'' +
                        ", url='" + url + '\'' +
                        '}';
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }



}
