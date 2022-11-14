/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DaiyoseiSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Daiyosei extends Mob {
	{
		spriteClass = DaiyoseiSprite.class;
		HP = HT = 20;
		defenseSkill = 5;
		EXP = 4;
		maxLvl = 9;
        //Maybe an item that can heal you a bit
        loot = ScrollOfLullaby.class;
		lootChance = 0.15f;
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 3 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(4, 6);
	}

    @Override
	public float speed() {
		return super.speed();
	}


	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		for (int p : PathFinder.NEIGHBOURS8){
			Char ch = Actor.findChar(p+ this.pos);
			if (ch.alignment == this.alignment){
				ch.HP += 1;
				Buff.affect(ch, Bless.class, 5f);
			}
		}
		return damage;
	}

	@Override
	public void die(Object cause) {
		for (int p : PathFinder.NEIGHBOURS8){
			Char ch = Actor.findChar(p + this.pos);
			if (ch.alignment == this.alignment){
				ch.HP += 5;
				Buff.affect(ch, Bless.class, 10f);
			}
		}
		super.die(cause);
	}
}
