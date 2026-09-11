const items = require("items");

const originCore = new CoreBlock("origin-core");
exports.originCore = originCore
Object.assign(originCore, {
requirements: ItemStack.with(
    items.nickel, 4000,
    Items.silicon, 2000,
    Items.graphite, 1000,
)
})

/*const wulfrumCore = new CoreBlock("wulfrum-core");
exports.wulfrumCore = wulfrumCore
Object.assign(wulfrumCore, {
requirements: ItemStack.with(
    items.wulfrumSchematics, 8,
    items.wulfrumSteel, 800,
    Items.silicon, 800,
    Items.graphite, 600,
)
})
*/