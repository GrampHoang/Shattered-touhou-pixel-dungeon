package com.shatteredpixel.shatteredpixeldungeon.items.encounters;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class TenshiEnc extends EncounterNotes {
    {
		image = ItemSpriteSheet.HISOUBLADE_FIRE;
	}

	public void setSeen(){
		Catalog.setSeen(TenshiEnc.class);
	}

	public String npc(){
		return "Tenshi";
	}
}