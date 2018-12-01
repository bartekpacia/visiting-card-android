package com.community.jboss.visitingcard.networking;

public class VisitingCard
{
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private byte[] profileImage;

    public String getName()
    {
        return name;
    }

    public VisitingCard()
    {
    }

    public VisitingCard(String name, String surname, String phoneNumber, byte[] profileImage)
    {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }
}
