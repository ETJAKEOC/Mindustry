package mindustry.desktop.steam;

import static mindustry.Vars.service;

import arc.Events;
import arc.util.Log;
import arc.util.Timer;
import mindustry.game.EventType.ClientLoadEvent;
import steamworks.SteamResult;
import steamworks.SteamUserStats;
import steamworks.SteamUserStatsCallback;

public class SStats implements SteamUserStatsCallback {
	public final SteamUserStats stats = new SteamUserStats(this);

	private boolean updated = false;
	private int statSavePeriod = 2; //in minutes

	public SStats() {
		service.init();

		Events.on(ClientLoadEvent.class, e -> {
			Timer.schedule(() -> {
				if (updated) {
					stats.storeStats();
				}
			}, statSavePeriod * 60, statSavePeriod * 60);
		});
	}

	public void onUpdate() {
		this.updated = true;
	}

	@Override
	public void onUserStatsStored(long gameID, SteamResult result) {
		Log.info("Stored stats: @", result);

		if (result == SteamResult.OK) {
			updated = false;
		}
	}
}
