package mindustry.ui;

import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Scl;
import arc.util.Tmp;
import mindustry.gen.*;
import mindustry.graphics.Pal;

public class BorderImage extends Image{
    public float thickness = 4f, pad = 0f;
    public Color borderColor = Pal.gray;
    public boolean forceNearest = false, drawAlpha = false;
    public Color alphaColor = Color.gray.cpy();

    public BorderImage(){

    }

    public BorderImage(Texture texture){
        super(texture);
    }

    public BorderImage(Texture texture, float thick){
        super(texture);
        thickness = thick;
    }

    public BorderImage(TextureRegion region, float thick){
        super(region);
        thickness = thick;
    }

    public BorderImage(Drawable region){
        super(region);
    }

    public BorderImage border(Color color){
        this.borderColor = color;
        return this;
    }

    @Override
    public void draw(){
        TextureFilter prevMin = TextureFilter.linear, prevMag = TextureFilter.linear;

        if(forceNearest && getDrawable() instanceof TextureRegionDrawable draw){
            prevMin = draw.getRegion().texture.getMinFilter();
            prevMag = draw.getRegion().texture.getMagFilter();
            draw.getRegion().texture.setFilter(TextureFilter.nearest);
        }
        if(drawAlpha){
            Draw.color(alphaColor, parentAlpha);
            Vec2 v = scaling.apply(imageWidth, imageHeight, width, height).scl(1f / width, 1f / height);
            TextureRegion region = ((TextureRegionDrawable)Tex.alphaBg).getRegion();
            Tmp.tr1.set(region.texture);
            Tmp.tr1.set(region.u, region.v, Mathf.lerp(region.u, region.u2, v.x), Mathf.lerp(region.v, region.v2, v.y));
            Draw.rect(Tmp.tr1, x + imageX + imageWidth * scaleX/2f, y + imageY + imageHeight * scaleY/2f, imageWidth * scaleX, imageHeight * scaleY);
        }

        super.draw();

        Draw.color(borderColor);
        Draw.alpha(parentAlpha);
        Lines.stroke(Scl.scl(thickness));
        Lines.rect(x + imageX - pad, y + imageY - pad, imageWidth * scaleX + pad*2, imageHeight * scaleY + pad*2);
        Draw.reset();

        if(forceNearest && getDrawable() instanceof TextureRegionDrawable draw){
            draw.getRegion().texture.setFilter(prevMin, prevMag);
        }
    }
}
