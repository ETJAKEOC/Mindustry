import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.mod.data.ContentAsset;
import mindustry.type.Item;
import mindustry.type.UnitType;

public class DataAssetTests {

	@BeforeAll
	static void init() {
		ApplicationTests.launchApplication(false);
	}

	static <T> T find(ContentType type, String name) {
		return (T) Vars.content.getByName(type, "dp-" + name);
	}

	static void loadContent(ContentType type, String name, String data) {
		Vars.state.data.load(Seq.with(new ContentAsset(name + ".json", type, data)));
	}

	@AfterEach
	void resetAfter() {
		Vars.logic.reset();
	}

	@BeforeEach
	void resetBefore() {
		Vars.logic.reset();
	}

	@Test
	void basicItem() {
		int totalItems = Vars.content.items().size;

		loadContent(ContentType.item, "testitem", """
				name: 'Test Item'
				hardness: 10
				""");

		Item it = find(ContentType.item, "testitem");
		assertNotNull(it);
		assertEquals(10f, it.hardness, 0.001f);
		assertEquals("Test Item", it.localizedName);

		resetAfter();

		assertEquals(totalItems, Vars.content.items().size, "Item content must be properly reset");
		assertNull(find(ContentType.item, "testitem"), "Item must be properly removed from map");
	}

	@Test
	void basicUnit() {

		loadContent(ContentType.unit, "testunit", """
				name: 'Test Unit'
				type: tank
				weapons: [
				    {
				        mirror: true
				        bullet: {
				            damage: 10
				            type: Laser
				            length: 1000
				        }
				    }
				]
				""");

		UnitType it = find(ContentType.unit, "testunit");

		assertNotNull(it);
		assertInstanceOf(TankUnit.class, it.create(Team.sharded));
		assertEquals("Test Unit", it.localizedName);
		assertEquals(2, it.weapons.size);
		assertEquals(LaserBulletType.class, it.weapons.get(0).bullet.getClass());
		assertEquals(1000f, ((LaserBulletType) it.weapons.get(0).bullet).length, 0.001f);
	}

	@Test
	void noContentAddedWithError() {

		loadContent(ContentType.block, "badblock", """
				name: 'This will explode'
				type: Bad
				""");

		assertNull(find(ContentType.block, "badblock"), "Content should not be loaded when an error occurs");
		assertEquals(1, Vars.state.data.getContent().first().warnings.size);
	}

	@Test
	void noNullFieldsAllowed() {

		loadContent(ContentType.block, "badblock", """
				name: 'This will explode'
				flags: null
				""");

		assertNull(find(ContentType.block, "badblock"), "Content should not be loaded when an error occurs");
		assertEquals(1, Vars.state.data.getContent().first().warnings.size);
		assertTrue(Vars.state.data.getContent().first().warnings.toString().contains("null"), "Warnings must contain mention of field being null: " + Vars.state.data.getContent().first().warnings);
	}
}
