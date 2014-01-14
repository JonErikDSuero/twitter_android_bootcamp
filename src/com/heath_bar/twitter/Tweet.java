package com.heath_bar.twitter;

public class Tweet {

    // Each tweet should include information such as:
    //   the tweet content,
    //   the creation time,
    //   the user who tweeted
    //   profile picture
	
    public String text;
    public String created_at;
    public String user_name;
    public String user_profile_image_url;

    public Tweet (String text, String created_at, String user_name, String user_profile_image_url) {
        this.text = text;
        this.created_at = created_at;
        this.user_name = user_name;
        this.user_profile_image_url = user_profile_image_url.replace("\\", "");
    }
        
}
