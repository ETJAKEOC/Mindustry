package mindustry.desktop.steam;

import steamworks.SteamUser;
import steamworks.SteamUserCallback;

public class SUser implements SteamUserCallback{
    public final SteamUser user = new SteamUser(this);
}
