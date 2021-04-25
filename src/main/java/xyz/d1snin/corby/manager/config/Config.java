package xyz.d1snin.corby.manager.config;

import java.awt.*;

public class Config {

    public String token;
    public String bot_prefix_default;
    public String bot_name;
    public String owner_id;
    public int default_cooldown_seconds;
    public String bot_pfp_url; //!
    public String invite_url; //!
    public String id; //!
    public String name_as_tag; //!
    public Color default_color;
    public Color error_color;
    public Color starboard_color;
    public String emote_trash;
    public String emote_star;
    public int default_starboard_stars;
    public boolean default_starboard_isenabled;

    public Config(

            String token,
            String bot_prefix_default,
            String bot_name,
            String owner_id,
            int default_cooldown_seconds,
            Color default_color,
            Color error_color,
            Color starboard_color,
            String emote_trash,
            String emote_star,
            int default_starboard_stars,
            boolean default_starboard_isenabled

    ) {

        this.token = token;
        this.bot_prefix_default = bot_prefix_default;
        this.bot_name = bot_name;
        this.owner_id = owner_id;
        this.default_cooldown_seconds = default_cooldown_seconds;
        this.default_color = default_color;
        this.error_color = error_color;
        this.starboard_color = starboard_color;
        this.emote_trash = emote_trash;
        this.emote_star = emote_star;
        this.default_starboard_stars = default_starboard_stars;
        this.default_starboard_isenabled = default_starboard_isenabled;

    }

    public void setBotPfpUrl(String bot_pfp_url) {
        this.bot_pfp_url = bot_pfp_url;
    }

    public void setInviteUrl(String invite_url) {
        this.invite_url = invite_url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNameAsTag(String name_as_tag) {
        this.name_as_tag = name_as_tag;
    }

    public static class ExitCodes {
        public static int NORMAL_SHUTDOWN_EXIT_CODE = 0;
        public static int CANT_CONNECT_TO_THE_DATABASE_EXIT_CODE = 11;
        public static int BAD_TOKEN_EXIT_CODE = 21;
        public static int BAD_CONFIG_EXIT_CODE = 22;
    }
}
