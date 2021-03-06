package riskOfSpire.actions.unique.relicEffects;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import riskOfSpire.vfx.combat.WobblyMissileEffect;


public class WobblyMissileAction extends AbstractGameAction {
    private DamageInfo info;
    private int damageCount = 0;
    public boolean doDamage = false;
    private int projectileCount;
    private int projectilesFired;
    private float projectileTimer = 0.0f;
    private float projectileDelay;
    private Color effectColor;
    private AbstractCreature source;
    private AttackEffect attackEffect;

    public WobblyMissileAction(AbstractCreature target, AbstractCreature source, DamageInfo info, int projectileCount, float projectileDelay, Color effectColor, AttackEffect attackEffect) {
        this.setValues(target, this.info = info);
        this.actionType = ActionType.DAMAGE;
        this.projectileCount = projectileCount;
        this.projectileDelay = projectileDelay;
        this.effectColor = effectColor;
        this.source = source;
        this.attackEffect = attackEffect;
    }

    @Override
    public void update() {
        if(target.isDeadOrEscaped()) {
            target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true);
            if(target == null) {
                this.isDone = true;
                return;
            }
        }
        projectileTimer -= Gdx.graphics.getDeltaTime();
        if (this.shouldCancelAction() && this.info.type != DamageInfo.DamageType.THORNS) {
            this.isDone = true;
            return;
        }
        if (projectilesFired < projectileCount && projectileTimer <= 0.0f) {
            AbstractDungeon.effectList.add(
                    new WobblyMissileEffect(source.drawX * Settings.scale, source.drawY * Settings.scale, target.hb.cX, target.hb.cY, this, effectColor.cpy(), 6000.0f)
            );
            projectilesFired++;
            projectileTimer = projectileDelay;
        }
        if (doDamage && damageCount < projectileCount) {
            damageCount++;
            doDamage = false;
            this.target.damageFlash = true;
            this.target.damageFlashFrames = 4;
            FlashAtkImgEffect coloredPoison = new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, attackEffect);
            ReflectionHacks.setPrivateInherited(coloredPoison, FlashAtkImgEffect.class, "color", effectColor.cpy());
            AbstractDungeon.effectList.add(coloredPoison);
            this.target.tint.color = effectColor.cpy();
            this.target.tint.changeColor(Color.WHITE.cpy());
            this.target.damage(this.info);
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        if (damageCount == projectileCount) {
            this.isDone = true;
        }
    }
}


/*
@Override
    public void update() {
        projectileTimer -= Gdx.graphics.getDeltaTime();
        if (this.shouldCancelAction() && this.info.type != DamageInfo.DamageType.THORNS) {
            this.isDone = true;
            return;
        }
        if (projectilesFired < projectileCount && projectileTimer <= 0.0f) {
            AbstractDungeon.effectList.add(
                    new WobblyMissileEffect(source.drawX * Settings.scale, source.drawY * Settings.scale, target.hb.cX, target.hb.cY, this, effectColor.cpy(), 3600.0f)
            );
            projectilesFired++;
            projectileTimer = projectileDelay;
        }
        if (doDamage && damageCount < projectileCount) {
            damageCount++;
            doDamage = false;
            this.target.damageFlash = true;
            this.target.damageFlashFrames = 4;
            FlashAtkImgEffect coloredPoison = new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, attackEffect);
            ReflectionHacks.setPrivateInherited(coloredPoison, FlashAtkImgEffect.class, "color", effectColor.cpy());
            AbstractDungeon.effectList.add(coloredPoison);
            this.target.tint.color = effectColor.cpy();
            this.target.tint.changeColor(Color.WHITE.cpy());
            this.target.damage(this.info);
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        if (damageCount == projectileCount || target.isDying) {
            this.isDone = true;
        }
    }

 */