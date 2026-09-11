/*pardon改进了这个文件*/
const items = require("items");
const liquids = require("liquids");
const core = require("core");

const Sura = new Planet("Sura", Planets.sun,5);
Sura.radius = 5;
Sura.orbitSpacing = 600;
Sura.bloom = true;
Sura.accessible = true
Sura.visible = true;
Sura.localizedName = "[#FFC64C]Sura"
Sura.orbitRadius = 470;
Sura.orbitTime = 2400 * 60;
Sura.rotateTime = 24 * 60;
Sura.meshLoader = () => new SunMesh(Sura,3, 6, 3.4, 2.8, 1.3, 0.8, 1.1,
    Color.valueOf("FF7A38"),
    Color.valueOf("FF9638"),
    Color.valueOf("FFC64C"),
    Color.valueOf("FFC64C"),
    Color.valueOf("FFE371"),
    Color.valueOf("F4EE8E")
);

/*const wulfrum = extend(Team,100,"wulfrum", Color.valueOf("55FBA5FF"),{})
exports.wulfrum = wulfrum;
*/

const Vitospp = new Planet("Vitospp", Sura , 1, 4);

Vitospp.meshLoader = prov(() => new MultiMesh(
    new NoiseMesh(Vitospp,0,5,0.94,1,0.0001,0.0001,1,Color.valueOf("649BE3"),Color.valueOf("86DCDC"),1,1,1,1)
));
Vitospp.cloudMeshLoader = prov(() => new MultiMesh(
    new HexSkyMesh(Vitospp,0,3,0.1,6,Color.valueOf("e8effa88"),3,0.3,1,0.43),
    new HexSkyMesh(Vitospp,0,2,0.11,6,Color.valueOf("e8effabb"),3,0.5,0.9,0.43),
    new HexSkyMesh(Vitospp,0,-2,0.034,5,Color.valueOf("35bda8"),0.4,0.2,0.2,0.4)
))
Vitospp.generator = extend(ErekirPlanetGenerator,{
getDefaultLoadout(){
return Schematics.readBase64("bXNjaAF4nGNgYWBhZmDJS8xNZeDOL8pMz8zTTc4vAnJSUouTizILSjLz8xgYGNhyEpNSc4oZmKJjGRlEkhNzEnMzSyp1kXUwMDCCEJAAANOhFfM=");
		}
	});

Vitospp.defaultCore = core.originCore;
//Vitospp.solarSystem = "[#FFC64C]Sura";
Vitospp.visible = true;
Vitospp.tidalLock = false;
Vitospp.accessible = true;
Vitospp.alwaysUnlocked = true;
Vitospp.allowLaunchLoadout = false;
Vitospp.clearSectorOnLose = true;
Vitospp.bloom = true;
Vitospp.startSector = 15;
Vitospp.allowLaunchSchematics = false;
Vitospp.orbitRadius = 50;
Vitospp.orbitTime = 60 * 60;
Vitospp.rotateTime = 24 * 60;
Vitospp.atmosphereRadIn = 0.08;
Vitospp.atmosphereRadOut = 0.8;
Vitospp.allowWaves = true;
Vitospp.allowWaveSimulation = true;
Vitospp.allowSectorInvasion = true;
Vitospp.allowCampaignRules = true;
Vitospp.allowLaunchToNumbered = false;
//Vitospp.atmosphereColor = Vitospp.lightColor = Color.valueOf("BCCEE3FF");
exports.Vitospp = Vitospp

const nodeRoot = TechTree.nodeRoot;
const nodeProduce = TechTree.nodeProduce;
const node = TechTree.node;
const SectorComplete = Objectives.SectorComplete;
const Research = Objectives.Research;

/*Vitospp.techTree = TechTree.nodeRoot("Wulfrum",core.wulfrumCore ,() => {

nodeProduce(Items.tungsten, () => {
nodeProduce(items.wulfrumSteel , () => {
nodeProduce(items.energyCore , () => {})
})

nodeProduce(Items.sand, () => {
nodeProduce(Items.graphite, () => {
nodeProduce(Items.silicon, () => {})
})
})
})
}),*/

Vitospp.techTree = TechTree.nodeRoot("Vitospp",core.originCore ,() => {

nodeProduce(items.nickel, () => {

nodeProduce(Items.tungsten, () => {
nodeProduce(items.chromium, () => {
nodeProduce(items.selfHealingAlloy, () => {
}),

nodeProduce(items.osmiridiumMine, () => {
nodeProduce(items.iridium, () => {}),
nodeProduce(items.osmium, () => {})
}),

nodeProduce(items.uraniumMine, () => {
nodeProduce(items.uranium, () => {
nodeProduce(Items.phaseFabric, () => {}),
nodeProduce(items.plutonium, () => {
nodeProduce(items.resonantCrystal, () => {})
})
})
})
})
}),

nodeProduce(Liquids.water, () => {
nodeProduce(Items.sporePod, () => {}),
nodeProduce(items.lithium, () => {}),
nodeProduce(Liquids.hydrogen, () => {
nodeProduce(Liquids.cryofluid, () => {}),

nodeProduce(Liquids.ozone, () => {
nodeProduce(liquids.helium, () => {}),
nodeProduce(Liquids.nitrogen, () => {
nodeProduce(Liquids.cyanogen, () => {})
})
})
})
}),

nodeProduce(Items.sand, () => {

nodeProduce(Items.metaglass, () => {}),

nodeProduce(Items.scrap, () => {
nodeProduce(Liquids.slag, () => {
nodeProduce(Items.surgeAlloy, () => {})
})
}),

nodeProduce(Items.coal, () => {
nodeProduce(Items.graphite, () => {
nodeProduce(Items.plastanium, () => {}),
nodeProduce(Items.silicon, () => {
nodeProduce(items.processorJunior, () => {
nodeProduce(items.processorSenior , () => {})
})
})
})
}),

nodeProduce(Liquids.oil, () => {
nodeProduce(Items.pyratite, () => {
nodeProduce(Items.blastCompound, () => {})
})
})
})
})
})