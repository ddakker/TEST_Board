/**
 * date :2008. 06. 12
 * author: ddakker@naver.com
 */
package dak.web.framework.user;

import java.util.Date;


public class UserProfile {
	String memberId				= null;
	String memberPasswd			= null;
	String memberName			= null;
	String memberPhone			= null;
	String memberMobile			= null;
	String homeZipcode  		= null;
	String homeAddress1	    	= null;
	String homeAddress2			= null;
	String officeZipcode		= null;
	String officeAddress1		= null;
	String officeAddress2		= null;
	String officePhone			= null;
	String officePhone2			= null;
	String memberEmail			= null;
	Date memberRegdate		= null;
	Date memberLatevisit		= null;
	String memberUse			= null;
	String memberIp				= null;
	String memberAdmin			= null;
	
	public String getMemberAdmin() {
		return memberAdmin;
	}
	public void setMemberAdmin(String memberAdmin) {
		this.memberAdmin = memberAdmin;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getMemberPasswd() {
		return memberPasswd;
	}
	public void setMemberPasswd(String memberPasswd) {
		this.memberPasswd = memberPasswd;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getMemberPhone() {
		return memberPhone;
	}
	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}
	public String getMemberMobile() {
		return memberMobile;
	}
	public void setMemberMobile(String memberMobile) {
		this.memberMobile = memberMobile;
	}
	public String getHomeZipcode() {
		return homeZipcode;
	}
	public void setHomeZipcode(String homeZipcode) {
		this.homeZipcode = homeZipcode;
	}
	public String getHomeAddress1() {
		return homeAddress1;
	}
	public void setHomeAddress1(String homeAddress1) {
		this.homeAddress1 = homeAddress1;
	}
	public String getHomeAddress2() {
		return homeAddress2;
	}
	public void setHomeAddress2(String homeAddress2) {
		this.homeAddress2 = homeAddress2;
	}
	public String getOfficeZipcode() {
		return officeZipcode;
	}
	public void setOfficeZipcode(String officeZipcode) {
		this.officeZipcode = officeZipcode;
	}
	public String getOfficeAddress1() {
		return officeAddress1;
	}
	public void setOfficeAddress1(String officeAddress1) {
		this.officeAddress1 = officeAddress1;
	}
	public String getOfficeAddress2() {
		return officeAddress2;
	}
	public void setOfficeAddress2(String officeAddress2) {
		this.officeAddress2 = officeAddress2;
	}
	public String getOfficePhone() {
		return officePhone;
	}
	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}
	public String getOfficePhone2() {
		return officePhone2;
	}
	public void setOfficePhone2(String officePhone2) {
		this.officePhone2 = officePhone2;
	}
	public String getMemberEmail() {
		return memberEmail;
	}
	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}
	public Date getMemberRegdate() {
		return memberRegdate;
	}
	public void setMemberRegdate(Date memberRegdate) {
		this.memberRegdate = memberRegdate;
	}
	public Date getMemberLatevisit() {
		return memberLatevisit;
	}
	public void setMemberLatevisit(Date memberLatevisit) {
		this.memberLatevisit = memberLatevisit;
	}
	public String getMemberUse() {
		return memberUse;
	}
	public void setMemberUse(String memberUse) {
		this.memberUse = memberUse;
	}
	public String getMemberIp() {
		return memberIp;
	}
	public void setMemberIp(String memberIp) {
		this.memberIp = memberIp;
	}
		
}
