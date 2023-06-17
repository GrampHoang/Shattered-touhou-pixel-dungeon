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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MoveDetect;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ThreeFairiesOfLight extends Mob {
    {
        HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 50 : 40;
		EXP = 4;

        defenseSkill = 8;
        state = WANDERING;
		viewDistance = 20;

        properties.add(Property.FAIRY);
		properties.add(Property.BOSS);
        immunities.add(Burning.class);
    }

    public int anger = 0;
    protected boolean charging_skill = false;

    @Override
	public int attackSkill( Char target ) {
		return 10;
	}

    @Override
	public boolean act() {
		if(Dungeon.hero != null && Dungeon.hero.buff(MoveDetect.class) != null && Dungeon.hero.justMoved){
			throwRock();
        }

        if (enemySeen && !isCharmedBy( enemy ) && canAttack( enemy )) {

            if (canUseReady()){
                return useReady();
            }
            if (canUseAbility()){
                return useAbility();
            }
            return doAttack( enemy );
            
        } else {
            if (canUseReady()){
                return useReady();
            }
            if (canUseAbility()){
                return useAbility();
            }

            if (enemySeen || charging_skill) {
                target = Dungeon.hero.pos;
            } else {
                sprite.showLost();
                charging_skill = false;
                state = WANDERING;
                return super.act();
            }
            
		    return super.act();
        }
	}

    @Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		// When super angry only do melee, else range attack only when no move detect
		if (anger < 2) return ( !(Dungeon.level.adjacent(pos, enemy.pos)) 
                                && attack.collisionPos == enemy.pos 
                                && (Dungeon.hero.buff(MoveDetect.class) == null))
                            // Being corner, they will retaliate
							|| canGetFurther(enemy.pos) == false;
		else return Dungeon.level.adjacent(pos, enemy.pos);
	}

    @Override
	public boolean doAttack(Char enemy) {
		if (!(Dungeon.level.adjacent(pos, enemy.pos))) spend(TICK/2);
		return super.doAttack(enemy);
	}

    protected boolean canGetFurther( int target ) {
		if (rooted || target == pos) {
			return false;
		}
		
		int step = Dungeon.flee( this, target, Dungeon.level.passable, fieldOfView, true );
		if (step != -1) {
			return true;
		} else {
			return false;
		}
	}

    @Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING && anger < 2) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}

	@Override
	public void aggro(Char ch) {
		if (ch == null || fieldOfView == null || fieldOfView[ch.pos]) {
			super.aggro(ch);
		}
	}

    protected void throwRock(){
		//Override
	}

    protected boolean canUseReady(){
        return false;
    }

    protected boolean useReady(){
        return false;
    }

    protected boolean canUseAbility(){
        return false;
    }

    protected boolean useAbility(){      
        return false;
    }

    public class Bullet extends Item {
		{
			image = ItemSpriteSheet.BULLET;
		}
	}
}
