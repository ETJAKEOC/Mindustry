const items = require("items");

Events.on(EventType.ClientLoadEvent, e => {
    Vars.renderer.planets.cam.far = 2400;
});
Planets.sun.radius = 92;
Planets.sun.orbitSpacing = 3000;
/*Planets.sun.accessible = true;
Planets.sun.alwaysUnlocked = true;
Planets.sun.visible = true;
Planets.sun.localizedName = "[#FFC64C]Lacinia";
Planets.sun.minZoom=0.1;
Planets.sun.grid = PlanetGrid.create(1);
Planets.sun.sectors.ensureCapacity(Planets.sun.grid.tiles.length);
for(let i = 0; i < Planets.sun.grid.tiles.length; i++){
                Planets.sun.sectors.add(new Sector(Planets.sun, Planets.sun.grid.tiles[i]));
            };*/

Planets.serpulo.orbitRadius = 370;
SectorPresets.groundZero.captureWave = 20;
SectorPresets.groundZero.difficulty = 2;

SectorPresets.frozenForest.captureWave = 25;
SectorPresets.frozenForest.difficulty = 2;

SectorPresets.craters.captureWave = 20;
SectorPresets.craters.difficulty = 4;

Planets.erekir.orbitRadius = 130;

Planets.tantros.orbitRadius = 350;
Planets.tantros.visible = true;