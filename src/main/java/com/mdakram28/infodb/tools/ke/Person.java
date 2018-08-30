package com.mdakram28.infodb.tools.ke;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Person {
	public String photo;
	public String name;
	public Date dateOfBirth;
	public Date dateOfDeath;
	public int age;
	public String website;
	public List<String> citizenship = new ArrayList<>();;
	public List<String> knownFor = new ArrayList<>();;
	public List<String> address = new ArrayList<>();;
	public List<String> profession = new ArrayList<>();;
	public List<String> spouses = new ArrayList<>();;
	public List<String> children = new ArrayList<>();;
	public List<String> parents = new ArrayList<>();
	
	public void addCitizenship(String c){
		citizenship.add(c);
	}
	public void addKnownFor(String kf) {
		knownFor.add(kf);
	}
	public void addAddress(String a) {
		address.add(a);
	}
	public void addProfession(String p) {
		profession.add(p);
	}
 	public void addSpouse(String s){
 		spouses.add(s);
 	}
 	public void addChild(String child) {
 		children.add(child);
 	}
 	public void addParent(String p) {
 		parents.add(p);
 	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Date getDateOfDeath() {
		return dateOfDeath;
	}
	public void setDateOfDeath(Date dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public List<String> getCitizenship() {
		return citizenship;
	}
	public void setCitizenship(List<String> citizenship) {
		this.citizenship = citizenship;
	}
	public List<String> getKnownFor() {
		return knownFor;
	}
	public void setKnownFor(List<String> knownFor) {
		this.knownFor = knownFor;
	}
	public List<String> getAddress() {
		return address;
	}
	public void setAddress(List<String> address) {
		this.address = address;
	}
	public List<String> getProfession() {
		return profession;
	}
	public void setProfession(List<String> profession) {
		this.profession = profession;
	}
	public List<String> getSpouses() {
		return spouses;
	}
	public void setSpouses(List<String> spouses) {
		this.spouses = spouses;
	}
	public List<String> getChildren() {
		return children;
	}
	public void setChildren(List<String> children) {
		this.children = children;
	}
	public List<String> getParents() {
		return parents;
	}
	public void setParents(List<String> parents) {
		this.parents = parents;
	};
	
	
	
}
