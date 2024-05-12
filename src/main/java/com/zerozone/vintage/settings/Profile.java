package com.zerozone.vintage.settings;

import com.zerozone.vintage.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class Profile {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    private String profileImageName;

    private String profileImageUrl;

    public Profile(Account account){
        this.bio = account.getBio();
        this.location = account.getLocation();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.profileImageName = account.getProfileImageName();
        this.profileImageUrl = account.getProfileImageUrl();
    }
}