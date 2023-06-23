/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * Gensokyo Pixel Dungeon
 * Copyright (C) 2022-2023 GrampHoang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LunaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;

public class Luna extends ThreeFairiesOfLight {

	{
		spriteClass = LunaSprite.class;
		anger = 0;
		charging_skill = false;
	}

	private int SKILL_COOLDOWN = 18;
	public int moon_cd = 1;

    @Override
	public int damageRoll() {
		return Random.NormalIntRange( 1*(anger+1), 3*(anger+1) );
	}

	//Luna have higher dodge
	@Override
	public int defenseSkill(Char enemy) {
		return (int)(super.defenseSkill(enemy) * ((anger > 0) ? 2 : 1.5f));
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

    @Override
	public int attackProc(Char enemy, int damage) {
        Buff.affect(enemy, Weakness.class, 5f);
		return super.attackProc(enemy, damage);
	}

    @Override
	public void damage( int dmg, Object src ) {
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				mob.beckon( this.pos );
			}
		}
		super.damage( dmg, src );
	}
    
	public void loseFriend(){
		this.anger++;
		this.moon_cd = 1;
		if (this.anger > 1) BossHealthBar.assignBoss(this);
	}

	@Override
	public void die( Object cause ) {
        if(anger > 1){
			Dungeon.level.drop( new ScrollOfAntiMagic(), pos ).sprite.drop();
			Dungeon.level.unseal();
			GameScene.bossSlain();
		}
		
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (mob instanceof Sunny) ((Sunny)mob).loseFriend();
			if (mob instanceof Star) ((Star)mob).loseFriend();
		}
		
        Statistics.bossScores[0] += 350;
		Statistics.bossScores[0] = Math.min(1050, Statistics.bossScores[0]);

		super.die( cause );
	}
	
    
    //////////////////////////////////////////////////////////////
    // SKILL
    /////

    @Override
    protected boolean canUseReady(){
        moon_cd--;
        if ((moon_cd <= 0 && (enemySeen || Dungeon.hero != null) && charging_skill == false)){
            return true;
        } else {
            return false;
        }
    }
    @Override
    protected boolean useReady(){
        if (Dungeon.hero != null){
            Dungeon.hero.interrupt();
        }
        GLog.n(Messages.get(this, "charging_skill"));
        spend(TICK);
        charging_skill = true;
        this.sprite.add(CharSprite.State.CHARGING);
        return true;
    }

	@Override
    protected boolean canUseAbility(){
        if(charging_skill == true){
            spend( TICK );
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean useAbility(){      
        return moonSilence();
    }

    private boolean moonSilence(){
        spend( TICK );
        charging_skill = false;
        this.sprite.remove(CharSprite.State.CHARGING);
        moon_cd = SKILL_COOLDOWN/ (anger +1);
        if (Dungeon.hero != null){
            //Silence, magic immue hero, basically prevent any magic thing
            //Then delete map
            Dungeon.hero.interrupt();
            Buff.affect(Dungeon.hero, Silence.class, 8f);
            Buff.affect(Dungeon.hero, MagicImmune.class, 8f);
            BArray.setFalse(Dungeon.level.visited);
			BArray.setFalse(Dungeon.level.mapped);
            GameScene.updateFog(); //just in case hero wasn't moved
            Dungeon.observe();
            }
        return true;
    }

	private final String ANGER = "angery"; //yes
	private final String MOON_CD = "sun__cooldown";
    private final String CHARGING_SKILL = "ready";

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		bundle.put( ANGER , anger );
		bundle.put( MOON_CD, moon_cd );
        bundle.put( CHARGING_SKILL, charging_skill );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {

		super.restoreFromBundle( bundle );

		anger = bundle.getInt( ANGER );
		if (state != SLEEPING) BossHealthBar.assignBoss(this);

		moon_cd = bundle.getInt(MOON_CD);
        charging_skill = bundle.getBoolean(CHARGING_SKILL);
	}

	@Override
	public String description() {
		String descript = super.description();
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			descript = descript + "\n\n_Badder Bosses:_\n" + Messages.get(this, "stronger_bosses");
		}
		return descript;
	}

    @Override
	protected void throwRock(){
		Dungeon.hero.interrupt();
		// Char ch = this;
            ((MissileSprite)this.sprite.parent.recycle( MissileSprite.class )).
            reset( this.pos, Dungeon.hero.pos, new Bullet(), new Callback() {
                @Override
                public void call() {
                    // ch.onAttackComplete();
					Dungeon.hero.damage( (isLunatic() ? Random.IntRange(1, anger*2+1) : anger*2+2), this);
                }
            } );
	}
}
