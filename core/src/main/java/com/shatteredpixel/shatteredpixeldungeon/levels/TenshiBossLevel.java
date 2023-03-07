package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.touhou.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class TenshiBossLevel extends Level {

    private static final int SIZE_W = 19;
    private static final int SIZE_H = 19;

    {
        color1 = 0xeeee00;
        color2 = 0x000000;

        viewDistance = 8;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_CITY;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CITY;
    }

    private static final Rect pillar1 = new Rect(5, 5, 7, 7);
    private static final Rect pillar2 = new Rect(12, 5, 14, 7);
    private static final Rect pillar3 = new Rect(5, 12, 7, 14);
    private static final Rect pillar4 = new Rect(12, 12, 14, 14);

    private static final Rect midpath = new Rect(9, 3, 10, 16);
    private static final Rect midpath2 = new Rect(3, 9, 16, 10);

    @Override
    protected boolean build() {

        setSize(17, 19);

        for (int i = 3; i < SIZE_H-3; i++) {
            for (int j = 3; j < SIZE_W-3; j++) {
                    map[i * width() + j] = Terrain.EMPTY;
                }
                
        }

        // // for (int i = 0; i < SIZE_H-1; i= i + 2) {
        // //     map[i * width() + 7] = Terrain.WALL_DECO;
        // //     map[i * width() + 9] = Terrain.WALL_DECO;                
        // // }

        // for (int i = 1; i < SIZE_W-1; i= i + 2) {
        //     map[8 * width() + i] = Terrain.STATUE;            
        // }
        
        Painter.fill(this, midpath, Terrain.EMPTY_SP);
        Painter.fill(this, midpath2, Terrain.EMPTY_SP);
        
        map[SIZE_H/2 + (SIZE_W-1)*width()/2] = Terrain.STATUE;

        Painter.fill(this, pillar1, Terrain.WALL);
        Painter.fill(this, pillar2, Terrain.WALL);
        Painter.fill(this, pillar3, Terrain.WALL);
        Painter.fill(this, pillar4, Terrain.WALL);
        
        entrance = 15 * width() + 9;
        exit     = 3 * width() + 9;
        map[entrance] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));

        return true;
    }

    @Override
    public void occupyCell( Char ch ) {
        super.occupyCell( ch );
        //If the hero is here
        // entrance still there = boss haven't appeared
        // Exit not there => Haven't triger and kill boss
        // And move away from the entrance
        if (map[entrance] == Terrain.ENTRANCE 
            && ch == Dungeon.hero
            && map[exit] != Terrain.EXIT
            && Dungeon.level.distance(ch.pos, entrance) > 2) {
            seal();
        }
    }

    @Override
    public void seal() {
        super.seal();
        set( entrance, Terrain.EMPTY_DECO );
        set( exit, Terrain.EMPTY_DECO );
        GameScene.updateMap( entrance );
        GameScene.updateMap( exit );

        Dungeon.observe();

        TenshiBoss mari = new TenshiBoss();
        mari.pos = (4 * width() + 15);;
        GameScene.add(mari);

    }

    @Override
    public void unseal() {
        super.unseal();
        set( entrance, Terrain.ENTRANCE );
        GameScene.updateMap( entrance );
        transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT));
        set( exit, Terrain.EXIT );
        GameScene.updateMap( exit );

        CellEmitter.get(exit-1).burst(ShadowParticle.UP, 25);
        CellEmitter.get(exit).burst(ShadowParticle.UP, 100);
        CellEmitter.get(exit+1).burst(ShadowParticle.UP, 25);

        Dungeon.observe();
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
    }

    @Override
    public int randomRespawnCell( Char ch ) {
        return entrance-width();
    }

    @Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
        bundle.put( "REMILIA_ENTRANCE", entrance );
        bundle.put( "REMILIA__EXIT", exit );
    }

    @Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
        entrance = bundle.getInt( "REMILIA_ENTRANCE" );
        exit = bundle.getInt( "REMILIA__EXIT" );
	}

}