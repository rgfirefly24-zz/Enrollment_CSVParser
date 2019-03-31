package com.company;

public class Enrollment implements Comparable<Enrollment>{
    private String userId;
    private String firstName;
    private String lastName;
    private Integer version;
    private String insuranceCompany;

    public String getUserId(){
        return userId;
    }

    public void setUserId(String id){
        userId = id;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public Integer getVersion(){
        return version;
    }

    public void setVersion(int version){
        this.version = version;
    }

    public String getFullName(){
        return String.format("%s %s", firstName,lastName);
    }

    public String getInsuranceCompany(){
        return insuranceCompany;
    }

    public void setInsuranceCompany(String company){
        insuranceCompany = company;
    }

    @Override
    public int compareTo(Enrollment o) {
        if(this.userId.equals(o.userId) && this.firstName.equals(o.firstName) && this.lastName.equals(o.lastName)
            && this.insuranceCompany.equals(o.insuranceCompany) && this.version > o.version) {return 1;}

        if(this.userId.equals(o.userId) && this.firstName.equals(o.firstName) && this.lastName.equals(o.lastName)
                && this.insuranceCompany.equals(o.insuranceCompany) && this.version < o.version) {return -1;}

        return 0;
    }
}
