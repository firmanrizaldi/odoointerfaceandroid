package com.example.odoointerface;

import java.util.Map;

public class hr_attende {
    public int getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public String getEmployee_name() {
        return Employee_name;
    }

    public void setEmployee_name(String employee_name) {
        Employee_name = employee_name;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private int employee_id ;
    private String Employee_name ;
    private String checkin ;
    private String checkout ;
    private Integer id ;

    public void setData(Map<String,Object> classObj) {
        M2Ofield employee_id = OdooUtility.getMany2One(classObj,"employee_id") ;
        setEmployee_id(employee_id.id);
        setEmployee_name(employee_id.value);
        setId((Integer) classObj.get("id"));
        setCheckin(OdooUtility.getString(classObj,"check_in"));
        setCheckout(OdooUtility.getString(classObj,"check_out"));

    }

}
