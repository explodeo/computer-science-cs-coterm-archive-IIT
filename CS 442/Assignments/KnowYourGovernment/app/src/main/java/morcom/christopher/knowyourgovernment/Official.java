package morcom.christopher.knowyourgovernment;

import java.io.Serializable;

public class Official implements Serializable {
    private String name, party, office, address, phone,
            website, email, photo, googlePlus, facebook, twitter, youtube;

    public Official(String name, String party, String office, String address, String phone, String website, String email, String photo, String googlePlus, String facebook, String twitter, String youtube) {
        this.name = name;
        this.party = party;
        this.office = office;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.email = email;
        this.photo = photo;
        this.googlePlus = googlePlus;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youtube = youtube;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getParty() {
        return party;
    }
    public void setParty(String party) {
        this.party = party;
    }
    public String getOffice() {
        return office;
    }
    public void setOffice(String office) {
        this.office = office;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
    public String getGooglePlus() {
        return googlePlus;
    }
    public void setGooglePlus(String googlePlus) {
        this.googlePlus = googlePlus;
    }
    public String getFacebook() {
        return facebook;
    }
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }
    public String getTwitter() {
        return twitter;
    }
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }
    public String getYoutube() {
        return youtube;
    }
    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    @Override
    public String toString(){
        return "Official{" +
                "name='" + name + '\'' +
                ", party='" + party + '\'' +
                ", office='" + office + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", googlePlus='" + googlePlus + '\'' +
                ", facebook='" + facebook + '\'' +
                ", twitter='" + twitter + '\'' +
                ", youtube='" + youtube + '\'' +
        '}';
    }
}
