package mindustry.ui.dialogs;

import static mindustry.Vars.player;
import static mindustry.Vars.playerColors;

import arc.func.Cons;
import arc.graphics.Color;
import arc.scene.ui.Dialog;
import arc.scene.ui.ImageButton;
import mindustry.gen.*;
import mindustry.ui.Styles;

public class PaletteDialog extends Dialog{
    private Cons<Color> cons;

    public PaletteDialog(){
        super("");
        build();
    }

    private void build(){
        cont.table(table -> {
            for(int i = 0; i < playerColors.length; i++){
                Color color = playerColors[i];

                ImageButton button = table.button(Tex.whiteui, Styles.squareTogglei, 34, () -> {
                    cons.get(color);
                    hide();
                }).size(48).get();
                button.setChecked(player.color().equals(color));
                button.getStyle().imageUpColor = color;

                if(i % 4 == 3){
                    table.row();
                }
            }
        });

        closeOnBack();
    }

    public void show(Cons<Color> cons){
        this.cons = cons;
        show();
    }
}
