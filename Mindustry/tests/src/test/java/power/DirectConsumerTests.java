package power;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import mindustry.content.Items;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGenerator.GeneratorBuild;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.production.GenericCrafter;

/**
 * Tests for direct power consumers.
 */
public class DirectConsumerTests extends PowerTestFixture {

	static int fakes = 0;

	@Test
	void noPowerRequestedWithNoItems() {
		testUnitFactory(0, 0, 0.08f, 0.08f, 1f);
	}

	@Test
	void noPowerRequestedWithInsufficientItems() {
		testUnitFactory(30, 0, 0.08f, 0.08f, 1f);
		testUnitFactory(0, 30, 0.08f, 0.08f, 1f);
	}

	@Test
	void powerRequestedWithSufficientItems() {
		testUnitFactory(30, 30, 0.08f, 0.08f, 1.0f);
	}

	void testUnitFactory(int siliconAmount, int leadAmount, float producedPower, float requestedPower, float expectedSatisfaction) {
		Tile ct = createFakeTile(0, 0, new GenericCrafter("fakefactory" + (fakes++)) {{
			hasPower = true;
			hasItems = true;
			consumePower(requestedPower);
			consumeItems(new ItemStack(Items.silicon, 30), new ItemStack(Items.lead, 30));
		}});
		ct.block().init();
		ct.build.items.add(Items.silicon, siliconAmount);
		ct.build.items.add(Items.lead, leadAmount);

		Tile producerTile = createFakeTile(2, 0, createFakeProducerBlock(producedPower));
		((GeneratorBuild) producerTile.build).productionEfficiency = 1f;

		PowerGraph graph = new PowerGraph();
		graph.add(producerTile.build);
		graph.add(ct.build);

		ct.build.update();
		graph.update();

		assertEquals(expectedSatisfaction, ct.build.power.status);
	}
}
