package riskOfSpire.powers.abstracts;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import riskOfSpire.RiskOfSpire;

public abstract class RoRStSTwoAmountPower extends TwoAmountPower {
    /**
     * @param bigImageName - is the name of the 84x84 image for your power.
     * @param smallImageName - is the name of the 32x32 image for your power.
     */
    public void setImage(String bigImageName, String smallImageName){
        String path = RiskOfSpire.assetPath("images/");

        String path128 = path + "powers/" + bigImageName;
        String path48 = path + "powers/" + smallImageName;

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);
    }

    /**
     * @param imgName - is the name of a 16x16 image. Example: setTinyImage("power.png");
     */
    public void setTinyImage(String imgName){
        String path = RiskOfSpire.assetPath("images/powers/");
        this.img = ImageMaster.loadImage(path + imgName);
    }
}
