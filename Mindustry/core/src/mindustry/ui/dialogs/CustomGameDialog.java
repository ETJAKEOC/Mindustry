package mindustry.ui.dialogs;

import mindustry.maps.Map;

public class CustomGameDialog extends MapListDialog{
    private MapPlayDialog dialog = new MapPlayDialog();

    public CustomGameDialog(){
        super("@customgame", false);
    }

    @Override
    void showMap(Map map){
        dialog.show(map);
    }
}
